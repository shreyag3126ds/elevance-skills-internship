package com.travel.pricing.model;

import java.time.LocalDate;

/** A bookable item (flight or hotel stay) with a base price and a travel date. */
public record Product(String id, String name, String type, double basePrice, LocalDate travelDate) {}
