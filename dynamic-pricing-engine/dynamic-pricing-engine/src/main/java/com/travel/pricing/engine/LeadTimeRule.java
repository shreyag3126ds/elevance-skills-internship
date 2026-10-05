package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Early-bird discount and last-minute surcharge. */
public class LeadTimeRule implements PricingRule {
    @Override
    public Adjustment evaluate(Product p, double demand, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, p.travelDate());
        if (days < 7)  return new Adjustment("Lead time", 1.12, "+12% - booking within 7 days of travel");
        if (days > 60) return new Adjustment("Lead time", 0.95, "-5% - early-bird (60+ days ahead)");
        return new Adjustment("Lead time", 1.0, "");
    }
}
