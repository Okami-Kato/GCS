package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class CertificateProperties {
    @NestedConfigurationProperty
    private SizeProperties name;
    @NestedConfigurationProperty
    private SizeProperties description;
    @NestedConfigurationProperty
    private IntegerProperties price;
    @NestedConfigurationProperty
    private IntegerProperties duration;
    @NestedConfigurationProperty
    private IntegerProperties tagAmount;

    public SizeProperties getName() {
        return name;
    }

    public void setName(SizeProperties name) {
        this.name = name;
    }

    public SizeProperties getDescription() {
        return description;
    }

    public void setDescription(SizeProperties description) {
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
