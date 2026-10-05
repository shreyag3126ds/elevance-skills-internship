package com.travel.pricing.model;

import java.util.List;

public record PriceQuote(String productId, double basePrice, double price, double multiplier,
                         List<Adjustment> adjustments, double demand, long time) {}
