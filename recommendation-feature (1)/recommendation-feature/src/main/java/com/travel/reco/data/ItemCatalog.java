package com.travel.reco.data;

import com.travel.reco.model.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemCatalog {
    private final Map<String, Item> items = new LinkedHashMap<>();

    public void add(Item item) { items.put(item.id(), item); }

    public Item get(String id) { return items.get(id); }

    public Collection<Item> all() { return items.values(); }
}
