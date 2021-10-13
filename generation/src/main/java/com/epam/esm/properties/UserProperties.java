package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class UserProperties {
    @NestedConfigurationProperty
    private SizeProperties firstName;
    @NestedConfigurationProperty
    private SizeProperties lastName;
    @NestedConfigurationProperty
    private SizeProperties login;
    @NestedConfigurationProperty
    private SizeProperties password;

    public SizeProperties getFirstName() {
        return firstName;
    }

    public void setFirstName(SizeProperties firstName) {
        this.firstName = firstName;
    }

    public SizeProperties getLastName() {
        return lastName;
    }

    public void setLastName(SizeProperties lastName) {
        this.lastName = lastName;
    }

    public SizeProperties getLogin() {
        return login;
    }

    public void setLogin(SizeProperties login) {
        this.login = login;
    }

    public SizeProperties getPassword() {
        return password;
    }

    public void setPassword(SizeProperties password) {
        this.password = password;
    }
}
