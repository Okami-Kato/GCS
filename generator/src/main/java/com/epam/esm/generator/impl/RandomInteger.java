package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;

import java.util.Random;

public class RandomInteger implements Generator<Integer> {
    private Integer min;
    private Integer max;

    public RandomInteger min(int min) {
        if (this.max != null && min > this.max)
            throw new IllegalArgumentException("min must be less or equal to max");
        this.min = min;
        return this;
    }

    public RandomInteger max(int max) {
        if (this.min != null && max < this.min)
            throw new IllegalArgumentException("max must be greater than min");
        this.max = max;
        return this;
    }

    @Override
    public Integer generate() {
        if (min == null)
            throw new IllegalStateException("min can't be null");
        if (max == null)
            throw new IllegalStateException("max can't be null");
        return (min + new Random().nextInt(max + 1 - min));
    }


}
