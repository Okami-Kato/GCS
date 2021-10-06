package com.epam.esm.generator.impl;

import java.util.List;
import java.util.TreeMap;

public class RandomSentence extends RandomWord {
    public RandomSentence(TreeMap<Integer, List<String>> dictionary) {
        super(dictionary);
    }

    @Override
    public String generate() {
        if (minSize == null) {
            throw new IllegalStateException("minSize can't be null");
        }
        if (maxSize == null) {
            throw new IllegalStateException("maxSize can't be null");
        }
        StringBuilder result = new StringBuilder();

        while (!(result.length() >= minSize && result.length() <= maxSize)) {
            result.append(new RandomWord(dictionary).withMinSize(0).withMaxSize(maxSize).generate());
            if (result.length() < maxSize) {
                result.append(" ");
            }
        }
        return result.toString();
    }
}
