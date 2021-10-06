package com.epam.esm.data_generation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tag")
public class TagProperties {
    private Integer amount;

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
