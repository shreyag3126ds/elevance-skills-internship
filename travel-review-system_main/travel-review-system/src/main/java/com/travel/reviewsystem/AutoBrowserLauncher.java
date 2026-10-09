package com.travel.reviewsystem;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * As soon as the Spring Boot app is fully up, this opens the Swagger UI
 * dashboard in your default browser automatically — so hitting Run in
 * IntelliJ takes you straight to the interactive API screen, with no
 * need to type the URL in by hand.
 */
@Component
public class AutoBrowserLauncher {

    private static final String DASHBOARD_URL = "http://localhost:8080/swagger-ui.html";

    @EventListener(ApplicationReadyEvent.class)
    public void openDashboard() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(DASHBOARD_URL));
            } else {
                // Headless environment (e.g. some servers/containers) - just log it instead
                System.out.println("Open the dashboard manually at: " + DASHBOARD_URL);
            }
        } catch (Exception e) {
            // Never let a browser-launch failure crash the app - just tell the user where to go
            System.out.println("Could not auto-open browser. Open the dashboard manually at: " + DASHBOARD_URL);
        }
    }
}
