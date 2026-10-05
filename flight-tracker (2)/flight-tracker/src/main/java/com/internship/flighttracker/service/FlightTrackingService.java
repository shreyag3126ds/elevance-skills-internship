package com.internship.flighttracker.service;

import com.internship.flighttracker.model.Flight;
import com.internship.flighttracker.model.TrackFlightRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the set of flights currently being tracked (in-memory for this demo -
 * swap for a database-backed repository in production, one row per user/flight).
 * Supports tracking multiple flights simultaneously, as required by the task.
 */
@Service
public class FlightTrackingService {

    private final ConcurrentHashMap<String, Flight> trackedFlights = new ConcurrentHashMap<>();

    public Flight track(TrackFlightRequest request) {
        Flight flight = new Flight(
                request.getFlightNumber(),
                request.getOrigin(),
                request.getDestination(),
                request.getScheduledDeparture(),
                request.getScheduledArrival()
        );
        trackedFlights.put(flight.getFlightNumber(), flight);
        return flight;
    }

    public boolean untrack(String flightNumber) {
        return trackedFlights.remove(flightNumber) != null;
    }

    public Optional<Flight> get(String flightNumber) {
        return Optional.ofNullable(trackedFlights.get(flightNumber));
    }

    public Collection<Flight> getAll() {
        return trackedFlights.values();
    }
}
