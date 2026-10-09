
package com.travel.reco;

import com.travel.reco.data.InteractionStore;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.data.SampleData;
import com.travel.reco.engine.HybridRecommendationService;
import com.travel.reco.feedback.FeedbackService;
import com.travel.reco.web.RecommendationServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        ItemCatalog catalog = new ItemCatalog();
        InteractionStore store = new InteractionStore();

        SampleData.load(catalog, store);

        FeedbackService feedback = new FeedbackService();
        HybridRecommendationService service =
                new HybridRecommendationService(catalog, store, feedback);

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8090")
        );

        RecommendationServer server =
                new RecommendationServer(port, service, feedback, catalog);

        server.start();

        System.out.println("Recommendation server started on port " + port);
    }
}