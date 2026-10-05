package com.internship.flighttracker.scheduler;

import com.internship.flighttracker.model.Flight;
import com.internship.flighttracker.model.StatusUpdate;
import com.internship.flighttracker.service.FlightTrackingService;
import com.internship.flighttracker.service.MockFlightApiService;
import com.internship.flighttracker.service.MockFlightApiService.MockApiResult;
import com.internship.flighttracker.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Simulates real-time flight tracking: every few seconds, polls the mock API
 * for each tracked flight, applies any status change, and pushes a notification
 * to connected clients when something meaningful happens (delay, boarding, etc).
 *
 * In a production system this interval would be minutes, not seconds - it's
 * shortened here so the "real-time" behaviour is visible during a demo.
 */
@Component
public class FlightUpdateScheduler {

    private final FlightTrackingService trackingService;
    private final MockFlightApiService mockApiService;
    private final NotificationService notificationService;

    public FlightUpdateScheduler(FlightTrackingService trackingService,
                                  MockFlightApiService mockApiService,
                                  NotificationService notificationService) {
        this.trackingService = trackingService;
        this.mockApiService = mockApiService;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 10_000) // poll every 10 seconds
    public void pollTrackedFlights() {
        for (Flight flight : trackingService.getAll()) {
            MockApiResult result = mockApiService.generateUpdate(flight);
            if (result == null) {
                continue; // no change this cycle
            }
            applyUpdate(flight, result);
        }
    }

    private void applyUpdate(Flight flight, MockApiResult result) {
        flight.setStatus(result.newStatus());
        flight.setDelayReason(result.delayReason());
        flight.setEstimatedArrival(result.revisedEstimatedArrival());
        flight.setLastUpdated(java.time.LocalDateTime.now());

        String message = buildMessage(flight, result);
        StatusUpdate update = new StatusUpdate(
                flight.getFlightNumber(),
                result.newStatus(),
                message,
                result.delayReason(),
                result.revisedEstimatedArrival()
        );

        notificationService.broadcast(update);
    }

    private String buildMessage(Flight flight, MockApiResult result) {
        return switch (result.newStatus()) {
            case DELAYED -> "Flight " + flight.getFlightNumber() + " delayed - " + result.delayReason();
            case BOARDING -> "Flight " + flight.getFlightNumber() + " is now boarding";
            case DEPARTED -> "Flight " + flight.getFlightNumber() + " has departed";
            case LANDED -> "Flight " + flight.getFlightNumber() + " has landed";
            case ON_TIME -> "Flight " + flight.getFlightNumber() + " is back on time";
            case CANCELLED -> "Flight " + flight.getFlightNumber() + " has been cancelled";
        };
    }
}
