package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;
import com.epam.esm.service.dto.request.CreateCertificateRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RandomCertificate implements Generator<CreateCertificateRequest> {
    private String name;
    private String description;
    private Integer price;
    private Integer duration;
    private Set<String> tagNames;


    public RandomCertificate() {
    }

    public RandomCertificate withName(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.name = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomCertificate withDescription(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
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

    public RandomCertificate withTagAmount(int minAmount, int maxAmount, List<String> availableTagsNames) {
        if (minAmount < 0)
            throw new IllegalArgumentException("minAmount must be positive");
        if (minAmount > maxAmount)
            throw new IllegalArgumentException("minAmount must be less or equal to maxAmount");
        Collections.shuffle(availableTagsNames);
        this.tagNames = new HashSet<>(
                availableTagsNames.subList(0, new RandomInteger().max(minAmount).min(maxAmount).generate())
        );
        return this;
    }

    @Override
    public CreateCertificateRequest generate() {
        if (name == null)
            throw new IllegalStateException("name can't be null");
        if (description == null)
            throw new IllegalStateException("description can't be null");
        if (price == null)
            throw new IllegalStateException("price can't be null");
        if (duration == null)
            throw new IllegalStateException("duration can't be null");

        return new CreateCertificateRequest(name, description, price, duration, tagNames);
    }
}
