package com.epam.esm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("generation")
public class GenerationProperties {
    private String dictionaryFile;
    private Integer certificateAmount;
    private Integer tagAmount;
    private Integer userAmount;
    private Integer userOrderAmount;
    @NestedConfigurationProperty
    private CertificateProperties certificate;
    @NestedConfigurationProperty
    private TagProperties tag;
    @NestedConfigurationProperty
    private UserProperties user;

    public String getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    public Integer getCertificateAmount() {
        return certificateAmount;
    }

    public void setCertificateAmount(Integer certificateAmount) {
        this.certificateAmount = certificateAmount;
    }

    public Integer getTagAmount() {
        return tagAmount;
    }

    public void setTagAmount(Integer tagAmount) {
        this.tagAmount = tagAmount;
    }

    public Integer getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(Integer userAmount) {
        this.userAmount = userAmount;
    }

    public Integer getUserOrderAmount() {
        return userOrderAmount;
    }

    public void setUserOrderAmount(Integer userOrderAmount) {
        this.userOrderAmount = userOrderAmount;
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
}
