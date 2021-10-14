package com.epam.esm.random.primitive;

import java.util.List;
import java.util.Map;

public class RandomSentence extends RandomWord {
    /**
     * Constructs random sentence
     * @param dictionary source of words
     * @param minSize min size of result sentence
     * @param maxSize max size of result sentence
     */
    public RandomSentence(Map<Integer, List<String>> dictionary, int minSize, int maxSize) {
        super(dictionary, minSize, maxSize);
    }

    @Override
    protected String extractValue(Map<Integer, List<String>> dictionary, int minSize, int maxSize) {
        StringBuilder result = new StringBuilder();

        while (!(result.length() >= minSize && result.length() <= maxSize)) {
            String word = new RandomWord(dictionary, 1, maxSize - result.length()).getValue();
            result.append(word);
            if (result.length() + 1 < minSize) {
                result.append(" ");
            }
        }
        return result.toString();
    }
}
