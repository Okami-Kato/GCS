package com.epam.esm.random.collection;

import com.epam.esm.random.primitive.RandomInteger;

import java.util.Collection;

public class RandomElementFromCollection<T> {
    private final T value;

    public RandomElementFromCollection(Collection<? extends T> collection) {
        this.value = extractValue(collection);
    }

    protected T extractValue(Collection<? extends T> collection) {
        int randomIndex = new RandomInteger(0, collection.size() - 1).getValue();
        T result = null;
        int currentIndex = 0;
        for (T element : collection) {
            result = element;
            if (currentIndex++ == randomIndex) {
                break;
            }
        }
        return result;
    }

    public T getValue() {
        return value;
    }
}
