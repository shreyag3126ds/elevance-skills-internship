package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.LocalDate;

/** A single pricing factor. Return multiplier 1.0 when the rule does not apply. */
public interface PricingRule {
    Adjustment evaluate(Product product, double demand, LocalDate today);
}
