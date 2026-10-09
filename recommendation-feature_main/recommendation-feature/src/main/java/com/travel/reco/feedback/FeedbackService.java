package com.travel.reco.feedback;

import com.travel.reco.model.Category;
import com.travel.reco.model.Feedback;
import com.travel.reco.model.Item;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Feedback loop: HELPFUL boosts the item's categories in that user's taste profile,
 * IRRELEVANT penalises them and hides the item. Changing your mind reverses the old effect.
 */
public class FeedbackService {
    private static final double HELPFUL_DELTA = 1.0;
    private static final double IRRELEVANT_DELTA = -1.5;

    private final Map<String, Map<String, Feedback>> given = new ConcurrentHashMap<>();
    private final Map<String, Map<Category, Double>> adjustments = new ConcurrentHashMap<>();

    public synchronized void submit(String userId, Item item, Feedback feedback) {
        Map<String, Feedback> userFeedback = given.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        Feedback previous = userFeedback.put(item.id(), feedback);
        Map<Category, Double> adj = adjustments.computeIfAbsent(userId, k -> new EnumMap<>(Category.class));
        if (previous != null) {
            for (Category c : item.categories()) adj.merge(c, -delta(previous), Double::sum);
        }
        for (Category c : item.categories()) adj.merge(c, delta(feedback), Double::sum);
    }

    public Feedback get(String userId, String itemId) {
        return given.getOrDefault(userId, Collections.emptyMap()).get(itemId);
    }

    public boolean isIrrelevant(String userId, String itemId) {
        return get(userId, itemId) == Feedback.IRRELEVANT;
    }

    public synchronized Map<Category, Double> adjustmentsFor(String userId) {
        return new EnumMap<>(adjustments.getOrDefault(userId, new EnumMap<>(Category.class)));
    }

    private static double delta(Feedback f) {
        return f == Feedback.HELPFUL ? HELPFUL_DELTA : IRRELEVANT_DELTA;
    }
}
