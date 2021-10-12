package com.epam.esm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("generation")
public class GenerationProperties {
    private String dictionaryFile;

    @NestedConfigurationProperty
    private CertificateProperties certificate;
    @NestedConfigurationProperty
    private TagProperties tag;
    @NestedConfigurationProperty
    private UserProperties user;
    @NestedConfigurationProperty
    private UserOrderProperties userOrder;

    public String getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    public CertificateProperties getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateProperties certificate) {
        this.certificate = certificate;
    }

    public TagProperties getTag() {
        return tag;
    }

    public void setTag(TagProperties tag) {
        this.tag = tag;
    }

    public UserProperties getUser() {
        return user;
    }

    public void setUser(UserProperties user) {
        this.user = user;
    }

    public UserOrderProperties getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(UserOrderProperties userOrder) {
        this.userOrder = userOrder;
    }
}
