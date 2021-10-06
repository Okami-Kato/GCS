package com.epam.esm.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("generator")
public class GeneratorProperties {
    private String dictionaryFile;

    public String getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }
}
