package com.travel.reco;

import com.travel.reco.data.InteractionStore;
import com.travel.reco.data.ItemCatalog;
import com.travel.reco.data.SampleData;
import com.travel.reco.engine.HybridRecommendationService;
import com.travel.reco.feedback.FeedbackService;
import com.travel.reco.web.RecommendationServer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;

public class Main {
    private static final int FIRST_PORT = 8090;   // avoids 8080 (flight-tracker)
    private static final int LAST_PORT = 8099;

    public static void main(String[] args) throws Exception {
        ItemCatalog catalog = new ItemCatalog();
        InteractionStore store = new InteractionStore();
        SampleData.load(catalog, store);
        FeedbackService feedback = new FeedbackService();
        HybridRecommendationService service = new HybridRecommendationService(catalog, store, feedback);

        RecommendationServer server = null;
        int port = FIRST_PORT;
        for (; port <= LAST_PORT; port++) {
            try {
                server = new RecommendationServer(port, service, feedback, catalog);
                break;
            } catch (BindException busy) {
                System.out.println("Port " + port + " is busy, trying the next one...");
            }
        }
        if (server == null) throw new IOException("No free port between " + FIRST_PORT + " and " + LAST_PORT);

        server.start();
        String url = "http://localhost:" + port + "/";
        System.out.println("Recommendations running at " + url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }
}
