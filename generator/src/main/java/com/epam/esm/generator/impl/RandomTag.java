package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;
import com.epam.esm.service.dto.request.CreateTagRequest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RandomTag implements Generator<CreateTagRequest> {
    private String name;

    public RandomTag withName(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.name = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    @Override
    public CreateTagRequest generate() {
        if (name == null)
            throw new IllegalStateException("name can't be null - set it first");
        return new CreateTagRequest(name);
    }
}
