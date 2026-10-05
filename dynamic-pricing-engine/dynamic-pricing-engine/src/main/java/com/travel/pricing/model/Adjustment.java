package com.travel.pricing.model;

/** One pricing factor applied to a price, kept so users can see exactly why a price changed. */
public record Adjustment(String rule, double multiplier, String reason) {
    public boolean isActive() {
        return Math.abs(multiplier - 1.0) > 1e-9;
    }
}
