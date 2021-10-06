package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;
import com.epam.esm.generator.exception.GeneratorException;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.epam.esm.generator.util.CollectionUtils.getRandomElementFromSet;

public class RandomWord implements Generator<String> {
    protected final TreeMap<Integer, List<String>> dictionary;
    protected Integer minSize;
    protected Integer maxSize;

    public RandomWord(TreeMap<Integer, List<String>> dictionary) {
        this.dictionary = dictionary;
    }

    public RandomWord withMinSize(int minSize) {
        if (minSize < 0) {
            throw new IllegalArgumentException("minSize must be positive");
        }
        if (this.maxSize != null && minSize > this.maxSize) {
            throw new IllegalArgumentException("minSize must be less or equal to maxSize");
        }
        this.minSize = minSize;
        return this;
    }

    public RandomWord withMaxSize(int maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        if (this.minSize != null && maxSize < this.minSize) {
            throw new IllegalArgumentException("maxSize must be greater or equal to minSize");
        }
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public String generate() {
        if (minSize == null) {
            throw new IllegalStateException("minSize can't be null");
        }
        if (maxSize == null) {
            throw new IllegalStateException("maxSize can't be null");
        }
        TreeSet<Integer> wordsSizes = new TreeSet<>(dictionary.keySet());

        Integer maxAcceptableWordLength = wordsSizes.last() < maxSize ? wordsSizes.last() : maxSize;
        Integer actualMaxWordLength = wordsSizes.floor(maxAcceptableWordLength);

        Integer minAcceptableWordLength = wordsSizes.first() > minSize ? wordsSizes.first() : minSize;
        Integer actualMinWordLength = wordsSizes.ceiling(minAcceptableWordLength);

        if (actualMaxWordLength == null || actualMinWordLength == null) {
            throw new GeneratorException(String.format("Dictionary doesn't contain words, consisting of %s-%s", minSize, maxSize));
        }
        Integer wordLength = getRandomElementFromSet(wordsSizes.subSet(actualMaxWordLength, true, actualMaxWordLength, true));
        List<String> words = dictionary.get(wordLength);
        return dictionary.get(wordLength).get(new RandomInteger().min(0).max(words.size()).generate());
    }
}
