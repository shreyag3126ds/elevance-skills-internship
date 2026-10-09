package com.travel.reco.engine;

import com.travel.reco.data.InteractionStore;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.feedback.FeedbackService;
import com.travel.reco.model.Category;
import com.travel.reco.model.Item;
import com.travel.reco.model.Scored;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/** Builds a category taste profile from history + feedback and ranks items by cosine similarity. */
public class ContentBasedRecommender implements Recommender {
    private final ItemCatalog catalog;
    private final InteractionStore store;
    private final FeedbackService feedback;

    public ContentBasedRecommender(ItemCatalog catalog, InteractionStore store, FeedbackService feedback) {
        this.catalog = catalog;
        this.store = store;
        this.feedback = feedback;
    }

    @Override
    public Map<String, Scored> score(String userId) {
        Map<Category, Double> profile = new EnumMap<>(Category.class);
        for (Map.Entry<String, Double> e : store.weightsFor(userId).entrySet()) {
            Item item = catalog.get(e.getKey());
            if (item == null) continue;
            for (Category c : item.categories()) profile.merge(c, e.getValue(), Double::sum);
        }
        feedback.adjustmentsFor(userId).forEach((c, v) -> profile.merge(c, v, Double::sum));
        profile.replaceAll((c, v) -> Math.max(0, v));

        double norm = Math.sqrt(profile.values().stream().mapToDouble(v -> v * v).sum());
        Map<String, Scored> result = new HashMap<>();
        if (norm == 0) return result;

        for (Item item : catalog.all()) {
            double dot = 0;
            Category best = null;
            double bestWeight = 0;
            for (Category c : item.categories()) {
                double w = profile.getOrDefault(c, 0.0);
                dot += w;
                if (w > bestWeight) { bestWeight = w; best = c; }
            }
            double score = dot / (norm * Math.sqrt(item.categories().size()));
            if (best != null && score > 0) {
                result.put(item.id(), new Scored(score,
                        "It matches your interest in " + best.label() + ", based on your bookings and activity.", best));
            }
        }
        return result;
    }
}
