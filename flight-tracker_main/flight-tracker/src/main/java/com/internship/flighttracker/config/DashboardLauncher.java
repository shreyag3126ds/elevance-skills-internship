package com.internship.flighttracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * As soon as the Spring Boot app has fully started, opens the dashboard
 * (served from src/main/resources/static/index.html) in the system's default
 * browser - so hitting Run in IntelliJ is all that's needed to see it.
 *
 * Falls back to just logging the URL if a desktop browser isn't available
 * (e.g. running headless on a server), rather than failing the app.
 */
@Component
public class DashboardLauncher {

    private static final Logger log = LoggerFactory.getLogger(DashboardLauncher.class);
    private static final String DASHBOARD_URL = "http://localhost:8080/";

    @EventListener(ApplicationReadyEvent.class)
    public void openDashboard() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(DASHBOARD_URL));
                log.info("Opened dashboard at {}", DASHBOARD_URL);
            } else {
                log.info("Desktop browsing not supported here - open {} manually", DASHBOARD_URL);
            }
        } catch (Exception e) {
            log.info("Could not auto-open browser - open {} manually ({})", DASHBOARD_URL, e.getMessage());
        }
    }
}
