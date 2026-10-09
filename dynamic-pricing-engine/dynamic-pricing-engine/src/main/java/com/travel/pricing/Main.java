
package com.travel.pricing;

import com.travel.pricing.model.Product;
import com.travel.pricing.service.PricingService;
import com.travel.pricing.web.PricingServer;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8090")
        );

        List<Product> products = List.of(
                new Product("FL-DEL-BOM", "Delhi to Mumbai flight",
                        "Flight", 5200, LocalDate.of(2026, 12, 24)),
                new Product("HT-GOA", "Goa beach resort (per night)",
                        "Hotel", 7800, LocalDate.of(2026, 10, 31)),
                new Product("FL-DEL-GOI", "Delhi to Goa flight",
                        "Flight", 6100, LocalDate.of(2027, 2, 10)),
                new Product("HT-MANALI", "Manali hill hotel (per night)",
                        "Hotel", 4500, LocalDate.of(2027, 6, 15))
        );

        PricingService service = new PricingService(products);
        PricingServer server = new PricingServer(service, port);

        service.start();
        server.start();

        System.out.println(
                "Dynamic Pricing Engine running on port " + port
        );
    }
}