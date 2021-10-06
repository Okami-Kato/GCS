package com.epam.esm.generator.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.generator.Generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class RandomCertificate implements Generator<Certificate> {
    private final List<Tag> availableTags;
    private String name;
    private String description;
    private Integer price;
    private Integer duration;
    private Set<Tag> tags;


    public RandomCertificate(List<Tag> availableTags) {
        this.availableTags = new ArrayList<>(availableTags);
    }

    public RandomCertificate withName(int minSize, int maxSize, TreeMap<Integer, List<String>> dictionary) {
        this.name = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomCertificate withDescription(int minSize, int maxSize, TreeMap<Integer, List<String>> dictionary) {
        this.description = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomCertificate withPrice(int min, int max) {
        if (min < 0)
            throw new IllegalArgumentException("min price must be positive");
        this.price = new RandomInteger().min(min).max(max).generate();
        return this;
    }

    public RandomCertificate withDuration(int min, int max) {
        if (min < 0)
            throw new IllegalArgumentException("min duration must be positive");
        this.duration = new RandomInteger().min(min).max(max).generate();
        return this;
    }

    public RandomCertificate withTagAmount(int minAmount, int maxAmount) {
        if (minAmount < 0)
            throw new IllegalArgumentException("minAmount must be positive");
        if (minAmount > maxAmount)
            throw new IllegalArgumentException("minAmount must be less or equal to maxAmount");
        Collections.shuffle(availableTags);
        this.tags = new HashSet<>(
                availableTags.subList(0, new RandomInteger().max(minAmount).min(maxAmount).generate())
        );
        return this;
    }

    @Override
    public Certificate generate() {
        if (name == null)
            throw new IllegalStateException("name can't be null");
        if (description == null)
            throw new IllegalStateException("description can't be null");
        if (price == null)
            throw new IllegalStateException("price can't be null");
        if (duration == null)
            throw new IllegalStateException("duration can't be null");
        if (tags == null)
            throw new IllegalStateException("tagNames can't be null");

        return new Certificate(name, description, price, duration, tags);
    }
}
