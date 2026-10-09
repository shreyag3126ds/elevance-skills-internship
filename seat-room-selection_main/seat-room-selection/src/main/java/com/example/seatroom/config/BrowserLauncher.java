
package com.example.seatroom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {

    private static final Logger log =
            LoggerFactory.getLogger(BrowserLauncher.class);

    @Value("${server.port:10000}")
    private int port;

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        log.info("Seat Room Selection application started successfully.");
        log.info("Configured server port: {}", port);
        log.info("Open your Render service URL in your browser.");
    }
}