package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.LocalDate;
import java.time.MonthDay;

/** Seasonal trend: summer peak is pricier, monsoon is cheaper. */
public class SeasonRule implements PricingRule {
    @Override
    public Adjustment evaluate(Product p, double demand, LocalDate today) {
        MonthDay m = MonthDay.from(p.travelDate());
        if (!m.isBefore(MonthDay.of(5, 15)) && !m.isAfter(MonthDay.of(6, 30))) {
            return new Adjustment("Season", 1.15, "+15% - summer peak season");
        }
        if (!m.isBefore(MonthDay.of(7, 1)) && !m.isAfter(MonthDay.of(9, 15))) {
            return new Adjustment("Season", 0.92, "-8% - monsoon off-season");
        }
        return new Adjustment("Season", 1.0, "");
    }
}
