package com.travel.reco.model;

public record Recommendation(Item item, double score, String headline, String reason, String feedback) { }
