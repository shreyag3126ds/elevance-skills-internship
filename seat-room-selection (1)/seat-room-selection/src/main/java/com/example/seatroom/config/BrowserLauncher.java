package com.example.seatroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher {

    @Value("${server.port:8080}")
    private int port;

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:" + port;
        System.out.println("App running at: " + url);
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            }
        } catch (Exception e) {
            System.out.println("Could not open browser automatically. Open " + url + " manually.");
        }
    }
}
