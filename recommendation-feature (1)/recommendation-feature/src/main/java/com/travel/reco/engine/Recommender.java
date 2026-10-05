package com.travel.reco.engine;

import com.travel.reco.model.Scored;

import java.util.Map;

public interface Recommender {
    /** @return itemId to score (0..1) with explanation, for items worth considering. */
    Map<String, Scored> score(String userId);
}
