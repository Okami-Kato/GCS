package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class UserProperties {
    private Integer amount;
    @NestedConfigurationProperty
    private StringProperties firstName;
    @NestedConfigurationProperty
    private StringProperties lastName;
    @NestedConfigurationProperty
    private StringProperties login;
    @NestedConfigurationProperty
    private StringProperties password;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public StringProperties getFirstName() {
        return firstName;
    }

    public void setFirstName(StringProperties firstName) {
        this.firstName = firstName;
    }

    public StringProperties getLastName() {
        return lastName;
    }

    public void setLastName(StringProperties lastName) {
        this.lastName = lastName;
    }

    public StringProperties getLogin() {
        return login;
    }

    public void setLogin(StringProperties login) {
        this.login = login;
    }

    public StringProperties getPassword() {
        return password;
    }

    public void setPassword(StringProperties password) {
        this.password = password;
    }
}
