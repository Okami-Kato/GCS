package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class CertificateProperties {
    private Integer amount;

    @NestedConfigurationProperty
    private StringProperties name;
    @NestedConfigurationProperty
    private StringProperties description;
    @NestedConfigurationProperty
    private IntegerProperties price;
    @NestedConfigurationProperty
    private IntegerProperties duration;
    @NestedConfigurationProperty
    private IntegerProperties tagAmount;

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

    public StringProperties getDescription() {
        return description;
    }

    public void setDescription(StringProperties description) {
        this.description = description;
    }

    public IntegerProperties getPrice() {
        return price;
    }

    public void setPrice(IntegerProperties price) {
        this.price = price;
    }

    public IntegerProperties getDuration() {
        return duration;
    }

    public void setDuration(IntegerProperties duration) {
        this.duration = duration;
    }

    public IntegerProperties getTagAmount() {
        return tagAmount;
    }

    public void setTagAmount(IntegerProperties tagAmount) {
        this.tagAmount = tagAmount;
    }
}
