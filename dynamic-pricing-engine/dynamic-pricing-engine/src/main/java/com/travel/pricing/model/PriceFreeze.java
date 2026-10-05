package com.travel.pricing.model;

public record PriceFreeze(String id, String productId, double lockedPrice, long expiresAt) {
    public boolean isActive() {
        return System.currentTimeMillis() < expiresAt;
    }
}
