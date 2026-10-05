package com.internship.flighttracker.model;

import java.time.LocalDateTime;

/**
 * Represents a single push notification about a flight status change.
 * This is the object sent to subscribed clients (e.g. over SSE) and is what
 * a real push-notification provider (FCM/APNs) would ultimately deliver.
 */
public class StatusUpdate {

    private final String flightNumber;
    private final FlightStatus status;
    private final String message;
    private final String delayReason;
    private final LocalDateTime revisedEstimatedArrival;
    private final LocalDateTime timestamp;

    public StatusUpdate(String flightNumber, FlightStatus status, String message,
                         String delayReason, LocalDateTime revisedEstimatedArrival) {
        this.flightNumber = flightNumber;
        this.status = status;
        this.message = message;
        this.delayReason = delayReason;
        this.revisedEstimatedArrival = revisedEstimatedArrival;
        this.timestamp = LocalDateTime.now();
    }

    public String getFlightNumber() { return flightNumber; }
    public FlightStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDelayReason() { return delayReason; }
    public LocalDateTime getRevisedEstimatedArrival() { return revisedEstimatedArrival; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
