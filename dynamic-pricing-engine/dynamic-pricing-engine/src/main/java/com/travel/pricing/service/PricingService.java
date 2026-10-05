package com.travel.pricing.service;

import com.travel.pricing.engine.DynamicPricingEngine;
import com.travel.pricing.model.BookingResult;
import com.travel.pricing.model.PriceFreeze;
import com.travel.pricing.model.PriceQuote;
import com.travel.pricing.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Orchestrates the engine: re-prices every product in real time and notifies all connected clients. */
public class PricingService {
    private static final long TICK_SECONDS = 3;

    private final DynamicPricingEngine engine = new DynamicPricingEngine();
    private final DemandTracker demandTracker = new DemandTracker();
    private final PriceHistoryService historyService = new PriceHistoryService();
    private final PriceFreezeService freezeService = new PriceFreezeService();
    private final Map<String, PriceQuote> latest = new ConcurrentHashMap<>();
    private final List<Consumer<Void>> listeners = new CopyOnWriteArrayList<>();
    private final List<Product> products;

    public PricingService(List<Product> products) {
        this.products = products;
        seedHistory();
        tick();
    }

    public void start() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pricing-ticker");
            t.setDaemon(true);
            return t;
        });
        ses.scheduleAtFixedRate(this::tick, TICK_SECONDS, TICK_SECONDS, TimeUnit.SECONDS);
    }

    /** Backfill a short price history so graphs are not empty on first load. */
    private void seedHistory() {
        Random rnd = new Random();
        long now = System.currentTimeMillis();
        int points = 30;
        for (Product p : products) {
            double d = 0.2;
            for (int i = points; i >= 1; i--) {
                d = Math.max(0, Math.min(1, d + (rnd.nextDouble() - 0.5) * 0.12));
                long t = now - i * TICK_SECONDS * 1000;
                PriceQuote q = engine.quote(p, d, LocalDate.now(), t);
                historyService.record(p.id(), t, q.price());
            }
        }
    }

    public void tick() {
        for (Product p : products) {
            double demand = demandTracker.tick(p.id());
            PriceQuote q = engine.quote(p, demand, LocalDate.now(), System.currentTimeMillis());
            latest.put(p.id(), q);
            historyService.record(p.id(), q.time(), q.price());
        }
        listeners.forEach(l -> l.accept(null));
    }

    public PriceFreeze freeze(String productId) {
        PriceQuote q = requireQuote(productId);
        return freezeService.freeze(productId, q.price());
    }

    public BookingResult book(String productId) {
        PriceQuote q = requireQuote(productId);
        PriceFreeze f = freezeService.getActive(productId);
        double paid = f != null ? f.lockedPrice() : q.price();
        freezeService.consume(productId);
        demandTracker.recordBooking(productId);
        return new BookingResult(productId, paid, q.price(), f != null);
    }

    private PriceQuote requireQuote(String productId) {
        PriceQuote q = latest.get(productId);
        if (q == null) throw new IllegalArgumentException("Unknown product: " + productId);
        return q;
    }

    public void addListener(Consumer<Void> l) { listeners.add(l); }
    public List<Product> getProducts() { return products; }
    public PriceQuote getQuote(String id) { return latest.get(id); }
    public PriceHistoryService getHistoryService() { return historyService; }
    public PriceFreezeService getFreezeService() { return freezeService; }
}
