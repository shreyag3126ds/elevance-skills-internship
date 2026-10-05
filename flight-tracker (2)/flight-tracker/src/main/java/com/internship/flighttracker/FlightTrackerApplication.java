package com.internship.flighttracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Live Flight Status feature.
 * Enables scheduling so the mock API can push periodic status updates.
 */
@SpringBootApplication
@EnableScheduling
public class FlightTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightTrackerApplication.class, args);
    }
}
