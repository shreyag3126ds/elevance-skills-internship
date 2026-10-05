package com.travel.reco.model;

/** Implicit feedback: stronger actions carry more weight. */
public enum InteractionType {
    BOOKED(3.0), SAVED(2.0), VIEWED(1.0);

    private final double weight;

    InteractionType(double weight) { this.weight = weight; }

    public double weight() { return weight; }
}
