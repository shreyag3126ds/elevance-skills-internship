package com.travel.reco.engine;

import com.travel.reco.data.InteractionStore;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.feedback.FeedbackService;
import com.travel.reco.model.Feedback;
import com.travel.reco.model.Item;
import com.travel.reco.model.Recommendation;
import com.travel.reco.model.Scored;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Blends content-based and collaborative scores. The collaborative share grows as more
 * interaction data arrives (0.3 up to 0.7), so accuracy improves over time.
 * New users with no history get popular items (cold start).
 */
public class HybridRecommendationService {
    private final ItemCatalog catalog;
    private final InteractionStore store;
    private final FeedbackService feedback;
    private final ContentBasedRecommender contentBased;
    private final CollaborativeRecommender collaborative;

    public HybridRecommendationService(ItemCatalog catalog, InteractionStore store, FeedbackService feedback) {
        this.catalog = catalog;
        this.store = store;
        this.feedback = feedback;
        this.contentBased = new ContentBasedRecommender(catalog, store, feedback);
        this.collaborative = new CollaborativeRecommender(catalog, store);
    }

    public List<Recommendation> recommend(String userId, int limit) {
        Map<String, Double> seen = store.weightsFor(userId);
        boolean coldStart = seen.isEmpty();
        Map<String, Scored> cb = contentBased.score(userId);
        Map<String, Scored> cf = collaborative.score(userId);
        double alpha = Math.min(0.7, 0.3 + store.totalInteractions() / 200.0);
        double maxPopularity = catalog.all().stream().mapToDouble(i -> store.popularity(i.id())).max().orElse(0);

        List<Recommendation> out = new ArrayList<>();
        for (Item item : catalog.all()) {
            if (seen.containsKey(item.id()) || feedback.isIrrelevant(userId, item.id())) continue;

            double score;
            String headline;
            List<String> reasons = new ArrayList<>();

            if (coldStart) {
                score = maxPopularity == 0 ? 0 : store.popularity(item.id()) / maxPopularity;
                headline = "Trending with travelers: " + item.name();
                reasons.add("You're new here, so we're showing what is most popular. "
                        + "Book or mark suggestions to teach us your taste.");
            } else {
                Scored a = cb.get(item.id());
                Scored b = cf.get(item.id());
                double s1 = a == null ? 0 : a.score();
                double s2 = b == null ? 0 : b.score();
                score = (1 - alpha) * s1 + alpha * s2;
                if (a != null && a.tag() != null) {
                    headline = "You liked " + a.tag().label() + "! Try " + item.name() + ".";
                    reasons.add(a.text());
                } else {
                    headline = "Travelers like you love " + item.name() + ".";
                }
                if (b != null) reasons.add(b.text());
                reasons.add(String.format("Match strength %.0f%% (%.0f%% your tastes, %.0f%% similar travelers).",
                        score * 100, (1 - alpha) * 100, alpha * 100));
            }
            if (score <= 0.01) continue;

            Feedback given = feedback.get(userId, item.id());
            out.add(new Recommendation(item, score, headline, String.join(" ", reasons),
                    given == null ? "" : given.name()));
        }
        out.sort(Comparator.comparingDouble(Recommendation::score).reversed());
        return out.size() > limit ? out.subList(0, limit) : out;
    }
}
