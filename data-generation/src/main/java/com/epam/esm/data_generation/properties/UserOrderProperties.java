package com.epam.esm.data_generation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("user-order")
public class UserOrderProperties {
    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
