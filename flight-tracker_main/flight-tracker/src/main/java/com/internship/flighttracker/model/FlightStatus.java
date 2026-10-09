package com.internship.flighttracker.model;

/**
 * Possible states a tracked flight can be in.
 * A real integration would map these to whatever an airline/aggregator API returns.
 */
public enum FlightStatus {
    ON_TIME,
    BOARDING,
    DELAYED,
    DEPARTED,
    LANDED,
    CANCELLED
}
