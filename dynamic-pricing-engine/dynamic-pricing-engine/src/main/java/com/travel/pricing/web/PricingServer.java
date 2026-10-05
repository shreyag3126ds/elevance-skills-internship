package com.travel.pricing.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.travel.pricing.model.BookingResult;
import com.travel.pricing.model.PriceFreeze;
import com.travel.pricing.model.Product;
import com.travel.pricing.service.PricingService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * HTTP API + live stream (Server-Sent Events). Every connected browser/app gets the same
 * updated prices at the same moment, which keeps displayed prices consistent across platforms.
 */
public class PricingServer {
    private final PricingService service;
    private final HttpServer server;
    private final List<OutputStream> clients = new CopyOnWriteArrayList<>();

    public PricingServer(PricingService service, int port) throws IOException {
        this.service = service;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::serveIndex);
        server.createContext("/api/prices", ex -> json(ex, 200, snapshot()));
        server.createContext("/api/stream", this::stream);
        server.createContext("/api/freeze", this::freeze);
        server.createContext("/api/book", this::book);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        service.addListener(v -> broadcast());
    }

    public void start() { server.start(); }

    private String snapshot() {
        String items = service.getProducts().stream().map(this::productJson).collect(Collectors.joining(","));
        return "{\"products\":[" + items + "],\"serverTime\":" + System.currentTimeMillis() + "}";
    }

    private String productJson(Product p) {
        return JsonWriter.product(p, service.getQuote(p.id()),
                service.getHistoryService().get(p.id()),
                service.getFreezeService().getActive(p.id()));
    }

    private void serveIndex(HttpExchange ex) throws IOException {
        if (!ex.getRequestURI().getPath().equals("/")) { json(ex, 404, JsonWriter.message(false, "Not found")); return; }
        try (InputStream in = getClass().getResourceAsStream("/static/index.html")) {
            if (in == null) { json(ex, 500, JsonWriter.message(false, "index.html missing from resources")); return; }
            byte[] body = in.readAllBytes();
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, body.length);
            ex.getResponseBody().write(body);
        } finally {
            ex.close();
        }
    }

    private void stream(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "text/event-stream");
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.sendResponseHeaders(200, 0);
        OutputStream out = ex.getResponseBody();
        clients.add(out);
        send(out, snapshot());      // first paint immediately
    }

    private void broadcast() {
        String data = snapshot();
        for (OutputStream out : clients) send(out, data);
    }

    private void send(OutputStream out, String data) {
        try {
            out.write(("data: " + data + "\n\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            clients.remove(out);    // client disconnected
        }
    }

    private void freeze(HttpExchange ex) throws IOException {
        if (!"POST".equals(ex.getRequestMethod())) { json(ex, 405, JsonWriter.message(false, "Use POST")); return; }
        try {
            PriceFreeze f = service.freeze(param(ex, "productId"));
            json(ex, 200, JsonWriter.message(true, String.format("Price locked at %.2f for %d minutes.",
                    f.lockedPrice(), com.travel.pricing.service.PriceFreezeService.FREEZE_MINUTES)));
            broadcast();
        } catch (IllegalStateException | IllegalArgumentException e) {
            json(ex, 400, JsonWriter.message(false, e.getMessage()));
        }
    }

    private void book(HttpExchange ex) throws IOException {
        if (!"POST".equals(ex.getRequestMethod())) { json(ex, 405, JsonWriter.message(false, "Use POST")); return; }
        try {
            BookingResult r = service.book(param(ex, "productId"));
            String msg = r.frozenPriceUsed()
                    ? String.format("Booked at your frozen price %.2f (current price is %.2f).", r.paid(), r.currentPrice())
                    : String.format("Booked at %.2f.", r.paid());
            json(ex, 200, JsonWriter.message(true, msg));
            broadcast();
        } catch (IllegalArgumentException e) {
            json(ex, 400, JsonWriter.message(false, e.getMessage()));
        }
    }

    private String param(HttpExchange ex, String name) {
        Map<String, String> params = new HashMap<>();
        String q = ex.getRequestURI().getQuery();
        if (q != null) for (String kv : q.split("&")) {
            String[] p = kv.split("=", 2);
            if (p.length == 2) params.put(p[0], p[1]);
        }
        String v = params.get(name);
        if (v == null) throw new IllegalArgumentException("Missing parameter: " + name);
        return v;
    }

    private void json(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}
