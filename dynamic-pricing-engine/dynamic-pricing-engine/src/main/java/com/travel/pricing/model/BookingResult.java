package com.travel.pricing.model;

public record BookingResult(String productId, double paid, double currentPrice, boolean frozenPriceUsed) {}
