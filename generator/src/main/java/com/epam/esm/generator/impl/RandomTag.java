package com.epam.esm.generator.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.generator.Generator;

import java.util.List;
import java.util.TreeMap;

public class RandomTag implements Generator<Tag> {
    private String name;

    public RandomTag withName(int minSize, int maxSize, TreeMap<Integer, List<String>> dictionary) {
        this.name = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    @Override
    public Tag generate() {
        if (name == null)
            throw new IllegalStateException("name can't be null - set it first");
        return new Tag(name);
    }
}
