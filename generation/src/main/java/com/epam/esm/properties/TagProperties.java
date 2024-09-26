package com.epam.esm.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class TagProperties {
    @NestedConfigurationProperty
    private SizeProperties name;

    public SizeProperties getName() {
        return name;
    }

    public void setName(SizeProperties name) {
        this.name = name;
    }
}
