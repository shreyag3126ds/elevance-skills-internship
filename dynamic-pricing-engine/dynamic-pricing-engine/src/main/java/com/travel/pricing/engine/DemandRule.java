package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.LocalDate;

/** Up to +25% as demand (0..1) rises. */
public class DemandRule implements PricingRule {
    @Override
    public Adjustment evaluate(Product p, double demand, LocalDate today) {
        double m = 1.0 + 0.25 * demand;
        double pct = (m - 1.0) * 100;
        return new Adjustment("Demand", Math.round(m * 1000) / 1000.0,
                pct < 0.5 ? "" : String.format("+%.1f%% - current booking demand", pct));
    }
}
