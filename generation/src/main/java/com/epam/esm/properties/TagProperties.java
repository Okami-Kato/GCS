package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class TagProperties {
    private Integer amount;

    @NestedConfigurationProperty
    private StringProperties name;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public StringProperties getName() {
        return name;
    }

    public void setName(StringProperties name) {
        this.name = name;
    }
}
