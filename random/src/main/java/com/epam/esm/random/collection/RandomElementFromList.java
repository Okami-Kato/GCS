package com.epam.esm.random.collection;

import com.epam.esm.random.primitive.RandomInteger;

import java.util.Collection;
import java.util.List;

public class RandomElementFromList<T> extends RandomElementFromCollection<T> {
    public RandomElementFromList(List<? extends T> list) {
        super(list);
    }

    @Override
    protected T extractValue(Collection<? extends T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List can't be empty");
        }
        return ((List<? extends T>) list).get(new RandomInteger(0, list.size() - 1).getValue());
    }
}
