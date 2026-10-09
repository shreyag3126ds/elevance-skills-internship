package com.travel.reco.model;

/** Score from one algorithm, plus the human-readable reason and the category that drove it. */
public record Scored(double score, String text, Category tag) { }
