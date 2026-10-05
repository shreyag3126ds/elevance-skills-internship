package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.PriceQuote;
import com.travel.pricing.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Pure pricing logic: base price x all active rule multipliers (capped), with a full breakdown. */
public class DynamicPricingEngine {
    private static final double MIN_MULTIPLIER = 0.70;
    private static final double MAX_MULTIPLIER = 1.75;   // keeps prices predictable

    private final List<PricingRule> rules = new ArrayList<>();

    public DynamicPricingEngine() {
        rules.add(new HolidayRule());
        rules.add(new SeasonRule());
        rules.add(new WeekendRule());
        rules.add(new LeadTimeRule());
        rules.add(new DemandRule());
    }

    public void addRule(PricingRule rule) {
        rules.add(rule);
    }

    public PriceQuote quote(Product p, double demand, LocalDate today, long timeMillis) {
        double multiplier = 1.0;
        List<Adjustment> applied = new ArrayList<>();
        for (PricingRule rule : rules) {
            Adjustment a = rule.evaluate(p, demand, today);
            if (a.isActive()) {
                applied.add(a);
                multiplier *= a.multiplier();
            }
        }
        multiplier = Math.max(MIN_MULTIPLIER, Math.min(MAX_MULTIPLIER, multiplier));
        double price = Math.round(p.basePrice() * multiplier * 100) / 100.0;
        return new PriceQuote(p.id(), p.basePrice(), price, multiplier, applied, demand, timeMillis);
    }
}
