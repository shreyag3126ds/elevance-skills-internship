package com.travel.pricing.service;

import com.travel.pricing.model.PricePoint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceHistoryService {
    private static final int MAX_POINTS = 120;
    private final Map<String, Deque<PricePoint>> history = new HashMap<>();

    public synchronized void record(String productId, long time, double price) {
        Deque<PricePoint> q = history.computeIfAbsent(productId, k -> new ArrayDeque<>());
        q.addLast(new PricePoint(time, price));
        while (q.size() > MAX_POINTS) q.removeFirst();
    }

    public synchronized List<PricePoint> get(String productId) {
        return new ArrayList<>(history.getOrDefault(productId, new ArrayDeque<>()));
    }
}
