package com.epam.esm.random.collection;

import com.epam.esm.random.primitive.RandomInteger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomSubList<T> {
    private final List<T> value;

    /**
     * Constructs random subList of given collection
     *
     * @param collection targeted collection
     * @param minSize    min size of result list
     * @param maxSize    max size of result list
     * @throws IllegalArgumentException if !(0 < minSize <= maxSize <= collection.size())
     */
    public RandomSubList(Collection<T> collection, int minSize, int maxSize) {
        value = extractValue(collection, minSize, maxSize);
    }

    protected List<T> extractValue(Collection<T> collection, int minSize, int maxSize) {
        if (minSize < 0) {
            throw new IllegalArgumentException("minSize must be non-negative number");
        }
        if (minSize > maxSize) {
            throw new IllegalArgumentException("minSize must be less or equal to maxSize");
        }
        if (maxSize > collection.size()) {
            throw new IllegalArgumentException("maxSize must be less than collection size");
        }
        if (collection.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        return list.subList(0, new RandomInteger(minSize - 1, maxSize > collection.size() ? collection.size() - 1 : maxSize - 1).getValue());
    }

    public List<T> getValue() {
        return new ArrayList<>(value);
    }
}
