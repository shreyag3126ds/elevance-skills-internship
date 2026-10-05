package com.travel.pricing.web;

import com.travel.pricing.model.*;

import java.util.List;
import java.util.stream.Collectors;

/** Minimal hand-written JSON so the project needs no external libraries. */
public final class JsonWriter {
    private JsonWriter() {}

    public static String str(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public static String product(Product p, PriceQuote q, List<PricePoint> history, PriceFreeze f) {
        String adj = q.adjustments().stream()
                .map(a -> "{\"rule\":" + str(a.rule()) + ",\"multiplier\":" + a.multiplier()
                        + ",\"reason\":" + str(a.reason()) + "}")
                .collect(Collectors.joining(","));
        String hist = history.stream()
                .map(h -> "[" + h.time() + "," + h.price() + "]")
                .collect(Collectors.joining(","));
        String freeze = f == null ? "null"
                : "{\"id\":" + str(f.id()) + ",\"lockedPrice\":" + f.lockedPrice()
                + ",\"expiresAt\":" + f.expiresAt() + "}";
        return "{\"id\":" + str(p.id()) + ",\"name\":" + str(p.name()) + ",\"type\":" + str(p.type())
                + ",\"travelDate\":" + str(p.travelDate().toString())
                + ",\"basePrice\":" + q.basePrice() + ",\"price\":" + q.price()
                + ",\"multiplier\":" + q.multiplier() + ",\"demand\":" + q.demand()
                + ",\"adjustments\":[" + adj + "],\"history\":[" + hist + "],\"freeze\":" + freeze + "}";
    }

    public static String message(boolean ok, String message) {
        return "{\"ok\":" + ok + ",\"message\":" + str(message) + "}";
    }
}
