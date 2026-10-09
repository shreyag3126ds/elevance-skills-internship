package com.internship.flighttracker.controller;

import com.internship.flighttracker.model.Flight;
import com.internship.flighttracker.model.TrackFlightRequest;
import com.internship.flighttracker.service.FlightTrackingService;
import com.internship.flighttracker.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;

/**
 * REST API for the Live Flight Status feature.
 *
 *   POST   /api/flights/track          -> start tracking a flight
 *   DELETE /api/flights/track/{no}     -> stop tracking a flight
 *   GET    /api/flights                -> list all currently tracked flights + status
 *   GET    /api/flights/{no}           -> get one flight's current status
 *   GET    /api/flights/stream         -> SSE stream of live push notifications
 */
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightTrackingService trackingService;
    private final NotificationService notificationService;

    public FlightController(FlightTrackingService trackingService,
                             NotificationService notificationService) {
        this.trackingService = trackingService;
        this.notificationService = notificationService;
    }

    @PostMapping("/track")
    public ResponseEntity<Flight> track(@Valid @RequestBody TrackFlightRequest request) {
        Flight flight = trackingService.track(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flight);
    }

    @DeleteMapping("/track/{flightNumber}")
    public ResponseEntity<Void> untrack(@PathVariable String flightNumber) {
        boolean removed = trackingService.untrack(flightNumber);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Flight>> getAll() {
        return ResponseEntity.ok(trackingService.getAll());
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> getOne(@PathVariable String flightNumber) {
        return trackingService.get(flightNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Clients (dashboard / app) connect here to receive push notifications
     * the moment a tracked flight's status changes.
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter stream() {
        return notificationService.subscribe();
    }
}
