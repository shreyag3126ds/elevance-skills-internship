package com.internship.flighttracker.model;

import java.time.LocalDateTime;

/**
 * Represents a single flight being tracked by a user.
 * Mutable fields (status, delayReason, estimatedArrival, lastUpdated) change
 * as mock updates are applied by the scheduler.
 */
public class Flight {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime scheduledDeparture;
    private final LocalDateTime scheduledArrival;

    private FlightStatus status;
    private LocalDateTime estimatedArrival;
    private String delayReason;
    private LocalDateTime lastUpdated;

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime scheduledDeparture, LocalDateTime scheduledArrival) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledArrival = scheduledArrival;
        this.status = FlightStatus.ON_TIME;
        this.estimatedArrival = scheduledArrival;
        this.delayReason = null;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getScheduledDeparture() { return scheduledDeparture; }
    public LocalDateTime getScheduledArrival() { return scheduledArrival; }
    public FlightStatus getStatus() { return status; }
    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public String getDelayReason() { return delayReason; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public void setStatus(FlightStatus status) { this.status = status; }
    public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }
    public void setDelayReason(String delayReason) { this.delayReason = delayReason; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
