package com.travel.reco.engine;

import com.travel.reco.data.InteractionStore;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.model.Item;
import com.travel.reco.model.Scored;

import java.util.HashMap;
import java.util.Map;

/**
 * Item-based collaborative filtering: two items are similar when the same users interacted with both
 * (cosine similarity over the user-item matrix). Candidates are scored by similarity to what you liked.
 */
public class CollaborativeRecommender implements Recommender {
    private final ItemCatalog catalog;
    private final InteractionStore store;

    public CollaborativeRecommender(ItemCatalog catalog, InteractionStore store) {
        this.catalog = catalog;
        this.store = store;
    }

    @Override
    public Map<String, Scored> score(String userId) {
        Map<String, Double> mine = store.weightsFor(userId);
        Map<String, Scored> result = new HashMap<>();
        if (mine.isEmpty()) return result;

        double totalWeight = mine.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Item candidate : catalog.all()) {
            if (mine.containsKey(candidate.id())) continue;
            double sum = 0, bestContribution = 0;
            String bestId = null;
            for (Map.Entry<String, Double> e : mine.entrySet()) {
                double contribution = e.getValue() * similarity(e.getKey(), candidate.id());
                sum += contribution;
                if (contribution > bestContribution) { bestContribution = contribution; bestId = e.getKey(); }
            }
            if (bestId != null) {
                Item source = catalog.get(bestId);
                result.put(candidate.id(), new Scored(sum / totalWeight,
                        "Travelers who chose " + source.name() + " also chose " + candidate.name() + ".", null));
            }
        }
        return result;
    }

    private double similarity(String a, String b) {
        double dot = 0, na = 0, nb = 0;
        for (Map<String, Double> userWeights : store.all().values()) {
            double x = userWeights.getOrDefault(a, 0.0);
            double y = userWeights.getOrDefault(b, 0.0);
            dot += x * y;
            na += x * x;
            nb += y * y;
        }
        return (na == 0 || nb == 0) ? 0 : dot / Math.sqrt(na * nb);
    }
}
