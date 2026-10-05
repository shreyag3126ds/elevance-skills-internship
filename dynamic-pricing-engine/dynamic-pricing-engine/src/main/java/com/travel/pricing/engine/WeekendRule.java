package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Peak travel days (Fri-Sun) cost a little more. */
public class WeekendRule implements PricingRule {
    @Override
    public Adjustment evaluate(Product p, double demand, LocalDate today) {
        DayOfWeek d = p.travelDate().getDayOfWeek();
        if (d == DayOfWeek.FRIDAY || d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) {
            return new Adjustment("Peak day", 1.08, "+8% - weekend travel");
        }
        return new Adjustment("Peak day", 1.0, "");
    }
}
