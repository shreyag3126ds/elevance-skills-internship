package com.travel.reco.data;

import com.travel.reco.model.InteractionType;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** In-memory user-item matrix. Swap for a database later without touching the engines. */
public class InteractionStore {
    private static final double MAX_WEIGHT = 5.0;

    private final Map<String, Map<String, Double>> weights = new ConcurrentHashMap<>();
    private final AtomicInteger total = new AtomicInteger();

    public void record(String userId, String itemId, InteractionType type) {
        weights.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
               .merge(itemId, type.weight(), (a, b) -> Math.min(MAX_WEIGHT, a + b));
        total.incrementAndGet();
    }

    public Map<String, Double> weightsFor(String userId) {
        return weights.getOrDefault(userId, Collections.emptyMap());
    }

    public Map<String, Map<String, Double>> all() { return weights; }

    public int totalInteractions() { return total.get(); }

    public double popularity(String itemId) {
        double sum = 0;
        for (Map<String, Double> w : weights.values()) sum += w.getOrDefault(itemId, 0.0);
        return sum;
    }
}
