package me.none030.mortismissions.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChanceContainer<T extends Object> {

    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private final List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private final Random random = new Random();

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = random.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}
