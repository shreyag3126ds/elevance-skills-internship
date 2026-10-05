package com.travel.reco.model;

import java.util.Set;

public record Item(String id, String name, ItemType type, String location,
                   Set<Category> categories, int price) { }
