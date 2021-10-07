package com.epam.esm.generator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

@Configuration
@EnableConfigurationProperties(GeneratorProperties.class)
public class GeneratorConfig {
    @Bean
    @Qualifier("dictionary")
    @ConditionalOnProperty("generator.dictionary-file")
    public Map<Integer, List<String>> dictionary(GeneratorProperties generatorProperties) throws FileNotFoundException {
        TreeMap<Integer, List<String>> result = new TreeMap<>();
        Scanner scanner = new Scanner(new File(generatorProperties.getDictionaryFile()));
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (result.containsKey(word.length())) {
                result.get(word.length()).add(word);
            } else {
                result.put(word.length(), new ArrayList<>(Collections.singletonList(word)));
            }
        }
        return result;
    }
}
