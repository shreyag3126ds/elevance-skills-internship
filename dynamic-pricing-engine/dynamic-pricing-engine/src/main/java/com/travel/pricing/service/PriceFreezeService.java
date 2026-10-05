package com.travel.pricing.service;

import com.travel.pricing.model.PriceFreeze;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Lets a user lock the current price for a limited time (one freeze per product). */
public class PriceFreezeService {
    public static final long FREEZE_MINUTES = 10;
    private final Map<String, PriceFreeze> freezes = new ConcurrentHashMap<>();

    public PriceFreeze freeze(String productId, double currentPrice) {
        PriceFreeze existing = getActive(productId);
        if (existing != null) {
            throw new IllegalStateException("This price is already frozen.");
        }
        PriceFreeze f = new PriceFreeze(UUID.randomUUID().toString().substring(0, 8), productId,
                currentPrice, System.currentTimeMillis() + FREEZE_MINUTES * 60_000);
        freezes.put(productId, f);
        return f;
    }

    public PriceFreeze getActive(String productId) {
        PriceFreeze f = freezes.get(productId);
        if (f != null && !f.isActive()) {
            freezes.remove(productId);
            return null;
        }
        return f;
    }

    public void consume(String productId) {
        freezes.remove(productId);
    }
}
