package com.travel.pricing;

import com.travel.pricing.model.Product;
import com.travel.pricing.service.PricingService;
import com.travel.pricing.web.PricingServer;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8090;

        List<Product> products = List.of(
                new Product("FL-DEL-BOM", "Delhi to Mumbai flight", "Flight", 5200, LocalDate.of(2026, 12, 24)),
                new Product("HT-GOA", "Goa beach resort (per night)", "Hotel", 7800, LocalDate.of(2026, 10, 31)),
                new Product("FL-DEL-GOI", "Delhi to Goa flight", "Flight", 6100, LocalDate.of(2027, 2, 10)),
                new Product("HT-MANALI", "Manali hill hotel (per night)", "Hotel", 4500, LocalDate.of(2027, 6, 15)));

        PricingService service = new PricingService(products);
        PricingServer server = new PricingServer(service, port);
        service.start();
        server.start();

        String url = "http://localhost:" + port + "/";
        System.out.println("Dynamic Pricing Engine running at " + url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }
}
