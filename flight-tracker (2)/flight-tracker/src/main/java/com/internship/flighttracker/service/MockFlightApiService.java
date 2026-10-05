package com.internship.flighttracker.service;

import com.internship.flighttracker.model.Flight;
import com.internship.flighttracker.model.FlightStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Stands in for a real third-party flight-data API (e.g. FlightAware, AviationStack).
 * Each call to generateUpdate() randomly decides whether a flight's status should
 * change, and if so produces a plausible new status, delay reason and revised ETA,
 * exactly like the JSON payload a real provider would return.
 */
@Service
public class MockFlightApiService {

    private final Random random = new Random();

    private static final List<String> DELAY_REASONS = List.of(
            "Air traffic control delay",
            "Late arrival of inbound aircraft",
            "Adverse weather conditions",
            "Crew scheduling delay",
            "Runway congestion at destination",
            "Technical inspection"
    );

    /**
     * Simulates one poll of the external API for a given flight.
     * Returns null when the provider has nothing new to report (most common case),
     * mirroring how a real polling integration behaves.
     */
    public MockApiResult generateUpdate(Flight flight) {
        // 35% chance any given poll produces a change - tune as needed for demos
        if (random.nextInt(100) >= 35) {
            return null;
        }

        FlightStatus current = flight.getStatus();
        FlightStatus next = pickNextStatus(current);

        String reason = null;
        LocalDateTime revisedEta = flight.getEstimatedArrival();

        if (next == FlightStatus.DELAYED) {
            reason = DELAY_REASONS.get(random.nextInt(DELAY_REASONS.size()));
            int extraMinutes = 15 + random.nextInt(90); // 15-105 minute delay
            revisedEta = flight.getEstimatedArrival().plusMinutes(extraMinutes);
        } else if (next == FlightStatus.ON_TIME) {
            revisedEta = flight.getScheduledArrival();
        }

        return new MockApiResult(next, reason, revisedEta);
    }

    private FlightStatus pickNextStatus(FlightStatus current) {
        // Simplified realistic progression instead of pure randomness
        return switch (current) {
            case ON_TIME -> weightedPick(FlightStatus.BOARDING, FlightStatus.DELAYED);
            case BOARDING -> weightedPick(FlightStatus.DEPARTED, FlightStatus.DELAYED);
            case DELAYED -> weightedPick(FlightStatus.BOARDING, FlightStatus.DEPARTED, FlightStatus.ON_TIME);
            case DEPARTED -> FlightStatus.LANDED;
            case LANDED, CANCELLED -> current; // terminal states - no further change
        };
    }

    private FlightStatus weightedPick(FlightStatus... options) {
        return options[random.nextInt(options.length)];
    }

    /**
     * Result of a single mock API poll: the new status plus any extra context
     * (delay reason / revised ETA) needed to build a rich notification.
     */
    public record MockApiResult(FlightStatus newStatus, String delayReason, LocalDateTime revisedEstimatedArrival) {}
}
