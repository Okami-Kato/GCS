package com.epam.esm.random.primitive;

import com.epam.esm.random.collection.RandomElementFromCollection;
import com.epam.esm.random.collection.RandomElementFromList;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class RandomWord {
    private final String value;

    /**
     * Retrieves random word
     *
     * @param dictionary source of words
     * @param minSize    min size of result word
     * @param maxSize    max size of result word
     * @throws IllegalArgumentException if !(0 < minSize <= maxSize), or if dictionary doesn't contain
     */
    public RandomWord(Map<Integer, List<String>> dictionary, int minSize, int maxSize) {
        if (minSize <= 0) {
            throw new IllegalArgumentException("minSize must be positive");
        }
        if (minSize > maxSize) {
            throw new IllegalArgumentException("minSize must be less or equal to maxSize");
        }
        value = extractValue(dictionary, minSize, maxSize);
    }

    protected String extractValue(Map<Integer, List<String>> dictionary, int minSize, int maxSize) {
        TreeSet<Integer> wordsSizes = new TreeSet<>(dictionary.keySet());

        Integer maxAcceptableWordLength = wordsSizes.last() < maxSize ? wordsSizes.last() : maxSize;
        Integer actualMaxWordLength = wordsSizes.floor(maxAcceptableWordLength);

        Integer minAcceptableWordLength = wordsSizes.first() > minSize ? wordsSizes.first() : minSize;
        Integer actualMinWordLength = wordsSizes.ceiling(minAcceptableWordLength);

        if (actualMaxWordLength == null || actualMinWordLength == null) {
            throw new IllegalArgumentException(String.format("Dictionary doesn't contain words, consisting of %s-%s characters", minSize, maxSize));
        }
        Integer wordLength = new RandomElementFromCollection<>(
                wordsSizes.subSet(actualMinWordLength, true, actualMaxWordLength, true))
                .getValue();
        List<String> words = dictionary.get(wordLength);
        return new RandomElementFromList<>(words).getValue();
    }

    public String getValue() {
        return value;
    }
}
