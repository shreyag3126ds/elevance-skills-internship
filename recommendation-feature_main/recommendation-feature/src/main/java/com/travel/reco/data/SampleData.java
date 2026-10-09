package com.travel.reco.data;

import com.travel.reco.model.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.travel.reco.model.Category.*;
import static com.travel.reco.model.InteractionType.*;
import static com.travel.reco.model.ItemType.*;

/** Demo data so the feature works out of the box. */
public final class SampleData {
    public static final Map<String, String> USERS = new LinkedHashMap<>();

    static {
        USERS.put("u1", "Aanya (beach lover)");
        USERS.put("u2", "Rohan (mountain trekker)");
        USERS.put("u3", "Meera (city & food)");
        USERS.put("u4", "Dev (mixed)");
        USERS.put("u5", "New user (no history)");
    }

    private SampleData() { }

    public static void load(ItemCatalog c, InteractionStore s) {
        c.add(new Item("d-bali", "Bali", DESTINATION, "Indonesia", Set.of(BEACH, NATURE, CULTURE), 90));
        c.add(new Item("d-goa", "Goa", DESTINATION, "India", Set.of(BEACH, FOOD), 60));
        c.add(new Item("d-maldives", "Maldives", DESTINATION, "Maldives", Set.of(BEACH, LUXURY), 300));
        c.add(new Item("d-phuket", "Phuket", DESTINATION, "Thailand", Set.of(BEACH, ADVENTURE), 80));
        c.add(new Item("d-santorini", "Santorini", DESTINATION, "Greece", Set.of(BEACH, CULTURE, LUXURY), 220));
        c.add(new Item("d-manali", "Manali", DESTINATION, "India", Set.of(MOUNTAIN, ADVENTURE), 50));
        c.add(new Item("d-leh", "Leh-Ladakh", DESTINATION, "India", Set.of(MOUNTAIN, ADVENTURE, CULTURE), 70));
        c.add(new Item("d-swiss", "Swiss Alps", DESTINATION, "Switzerland", Set.of(MOUNTAIN, NATURE, LUXURY), 350));
        c.add(new Item("d-paris", "Paris", DESTINATION, "France", Set.of(CITY, CULTURE, FOOD), 200));
        c.add(new Item("d-tokyo", "Tokyo", DESTINATION, "Japan", Set.of(CITY, FOOD, CULTURE), 160));
        c.add(new Item("d-jaipur", "Jaipur", DESTINATION, "India", Set.of(CULTURE, FOOD), 55));
        c.add(new Item("h-taj-goa", "Beachfront Resort Goa", HOTEL, "Goa, India", Set.of(BEACH, LUXURY), 180));
        c.add(new Item("h-manali-lodge", "Alpine Lodge Manali", HOTEL, "Manali, India", Set.of(MOUNTAIN, NATURE), 75));
        c.add(new Item("f-del-dps", "Flight: Delhi to Bali", FLIGHT, "DEL to DPS", Set.of(BEACH, NATURE), 420));
        c.add(new Item("f-del-nrt", "Flight: Delhi to Tokyo", FLIGHT, "DEL to NRT", Set.of(CITY, FOOD), 650));

        s.record("u1", "d-goa", BOOKED);
        s.record("u1", "d-maldives", BOOKED);
        s.record("u1", "d-phuket", SAVED);
        s.record("u2", "d-manali", BOOKED);
        s.record("u2", "d-leh", BOOKED);
        s.record("u2", "d-swiss", VIEWED);
        s.record("u3", "d-paris", BOOKED);
        s.record("u3", "d-tokyo", BOOKED);
        s.record("u3", "d-jaipur", SAVED);
        s.record("u4", "d-goa", BOOKED);
        s.record("u4", "d-bali", BOOKED);
        s.record("u4", "h-taj-goa", BOOKED);
        s.record("u4", "d-santorini", VIEWED);
        s.record("u4", "d-leh", VIEWED);
    }
}
