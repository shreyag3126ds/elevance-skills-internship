package com.internship.flighttracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request body for POST /api/flights/track
 */
public class TrackFlightRequest {

    @NotBlank
    private String flightNumber;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private LocalDateTime scheduledDeparture;

    @NotNull
    private LocalDateTime scheduledArrival;

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getScheduledDeparture() { return scheduledDeparture; }
    public void setScheduledDeparture(LocalDateTime scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }

    public LocalDateTime getScheduledArrival() { return scheduledArrival; }
    public void setScheduledArrival(LocalDateTime scheduledArrival) { this.scheduledArrival = scheduledArrival; }
}
