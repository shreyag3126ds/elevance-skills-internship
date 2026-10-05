package com.travel.pricing.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks demand per product (0..1). Bookings push it up; it decays and drifts over time. */
public class DemandTracker {
    private final Map<String, Double> demand = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public double get(String productId) {
        return demand.getOrDefault(productId, 0.2);
    }

    public void recordBooking(String productId) {
        demand.merge(productId, 0.10, (a, b) -> Math.min(1.0, a + b));
    }

    /** Simulates market activity so the demo prices move on their own. */
    public double tick(String productId) {
        double d = get(productId);
        d = d * 0.98 + (random.nextDouble() - 0.5) * 0.08;
        d = Math.max(0.0, Math.min(1.0, d));
        demand.put(productId, d);
        return d;
    }
}
