package com.travel.reco.model;

public enum Category {
    BEACH("beaches"), CITY("city breaks"), MOUNTAIN("mountains"), CULTURE("culture"),
    ADVENTURE("adventure"), LUXURY("luxury stays"), FOOD("food trips"), NATURE("nature");

    private final String label;

    Category(String label) { this.label = label; }

    public String label() { return label; }
}
