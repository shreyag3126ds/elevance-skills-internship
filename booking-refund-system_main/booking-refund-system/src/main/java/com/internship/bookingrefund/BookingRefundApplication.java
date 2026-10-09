package com.internship.bookingrefund;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class BookingRefundApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingRefundApplication.class, args);
    }

    @Bean
    public CommandLineRunner openDashboard() {
        return args -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create("http://localhost:8080"));
                }
            } catch (Exception e) {
                System.out.println("Could not auto-open browser: " + e.getMessage());
            }
        };
    }
}
