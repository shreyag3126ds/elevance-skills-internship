package com.travel.pricing.engine;

import com.travel.pricing.model.Adjustment;
import com.travel.pricing.model.Product;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

/** +20% when the travel date falls in a holiday window. */
public class HolidayRule implements PricingRule {
    private static final double UPLIFT = 1.20;

    private record Window(MonthDay start, MonthDay end, String name) {
        boolean contains(LocalDate d) {
            MonthDay m = MonthDay.from(d);
            if (start.compareTo(end) <= 0) return !m.isBefore(start) && !m.isAfter(end);
            return !m.isBefore(start) || !m.isAfter(end);   // window wraps over new year
        }
    }

    private final List<Window> windows = List.of(
            new Window(MonthDay.of(12, 20), MonthDay.of(1, 3), "New Year holidays"),
            new Window(MonthDay.of(10, 28), MonthDay.of(11, 12), "Diwali season"),
            new Window(MonthDay.of(8, 12), MonthDay.of(8, 16), "Independence Day weekend"));

    @Override
    public Adjustment evaluate(Product p, double demand, LocalDate today) {
        for (Window w : windows) {
            if (w.contains(p.travelDate())) {
                return new Adjustment("Holiday", UPLIFT, "+20% - " + w.name());
            }
        }
        return new Adjustment("Holiday", 1.0, "");
    }
}
