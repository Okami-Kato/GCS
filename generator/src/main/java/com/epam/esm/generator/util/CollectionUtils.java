package com.epam.esm.generator.util;

import com.epam.esm.generator.impl.RandomInteger;

import java.util.Set;

public class CollectionUtils {
    public static <T> T getRandomElementFromSet(Set<? extends T> set) {
        int randomIndex = new RandomInteger().min(0).max(set.size() - 1).generate();
        T randomElement = null;
        int currentIndex = 0;
        for (T t : set) {
            randomElement = t;
            if (currentIndex++ == randomIndex)
                break;
        }
        return randomElement;
    }
}
