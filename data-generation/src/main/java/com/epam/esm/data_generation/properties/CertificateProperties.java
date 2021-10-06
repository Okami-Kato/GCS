package com.epam.esm.data_generation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("certificate")
public class CertificateProperties {
    private Integer amount;
    private StringProperties name;
    private StringProperties description;
    private IntegerProperties price;
    private IntegerProperties duration;
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
