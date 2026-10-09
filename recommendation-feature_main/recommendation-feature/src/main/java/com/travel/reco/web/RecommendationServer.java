package com.travel.reco.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.data.SampleData;
import com.travel.reco.engine.HybridRecommendationService;
import com.travel.reco.feedback.FeedbackService;
import com.travel.reco.model.Feedback;
import com.travel.reco.model.Item;
import com.travel.reco.model.Recommendation;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendationServer {
    private final HttpServer server;
    private final HybridRecommendationService service;
    private final FeedbackService feedback;
    private final ItemCatalog catalog;

    public RecommendationServer(int port, HybridRecommendationService service,
                                FeedbackService feedback, ItemCatalog catalog) throws IOException {
        this.service = service;
        this.feedback = feedback;
        this.catalog = catalog;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::index);
        server.createContext("/api/users", ex -> send(ex, 200, "application/json", usersJson()));
        server.createContext("/api/recommendations", this::recommendations);
        server.createContext("/api/feedback", this::feedback);
    }

    public void start() { server.start(); }

    private void index(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (!path.equals("/") && !path.equals("/index.html")) {
            send(ex, 404, "text/plain", "Not found");
            return;
        }
        try (InputStream in = getClass().getResourceAsStream("/static/index.html")) {
            if (in == null) { send(ex, 500, "text/plain", "index.html missing from resources"); return; }
            send(ex, 200, "text/html; charset=utf-8", new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private void recommendations(HttpExchange ex) throws IOException {
        Map<String, String> q = parseForm(ex.getRequestURI().getRawQuery());
        String userId = q.getOrDefault("userId", "");
        if (!SampleData.USERS.containsKey(userId)) { send(ex, 400, "application/json", "{\"error\":\"unknown user\"}"); return; }
        List<Recommendation> recs = service.recommend(userId, 6);
        String body = recs.stream().map(this::toJson).collect(Collectors.joining(",", "[", "]"));
        send(ex, 200, "application/json", body);
    }

    private void feedback(HttpExchange ex) throws IOException {
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "text/plain", "POST only"); return; }
        Map<String, String> form = parseForm(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        Item item = catalog.get(form.getOrDefault("itemId", ""));
        String userId = form.getOrDefault("userId", "");
        Feedback f;
        try {
            f = Feedback.valueOf(form.getOrDefault("feedback", ""));
        } catch (IllegalArgumentException e) {
            send(ex, 400, "application/json", "{\"error\":\"bad feedback\"}");
            return;
        }
        if (item == null || !SampleData.USERS.containsKey(userId)) {
            send(ex, 400, "application/json", "{\"error\":\"bad request\"}");
            return;
        }
        feedback.submit(userId, item, f);
        send(ex, 200, "application/json", "{\"ok\":true}");
    }

    private String usersJson() {
        return SampleData.USERS.entrySet().stream()
                .map(e -> "{\"id\":" + Json.str(e.getKey()) + ",\"name\":" + Json.str(e.getValue()) + "}")
                .collect(Collectors.joining(",", "[", "]"));
    }

    private String toJson(Recommendation r) {
        Item i = r.item();
        return "{\"id\":" + Json.str(i.id())
                + ",\"name\":" + Json.str(i.name())
                + ",\"type\":" + Json.str(i.type().name())
                + ",\"location\":" + Json.str(i.location())
                + ",\"price\":" + i.price()
                + ",\"score\":" + Math.round(r.score() * 100)
                + ",\"headline\":" + Json.str(r.headline())
                + ",\"reason\":" + Json.str(r.reason())
                + ",\"feedback\":" + Json.str(r.feedback()) + "}";
    }

    private static Map<String, String> parseForm(String raw) {
        Map<String, String> map = new HashMap<>();
        if (raw == null || raw.isBlank()) return map;
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return map;
    }

    private static void send(HttpExchange ex, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
}
