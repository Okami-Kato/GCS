package com.epam.esm.config;

import com.epam.esm.properties.GenerationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class GenerationConfig {
    @Bean
    @Qualifier("dictionary")
    @ConditionalOnProperty("generation.dictionary-file")
    public Map<Integer, List<String>> dictionary(GenerationProperties generationProperties) throws FileNotFoundException {
        TreeMap<Integer, List<String>> result = new TreeMap<>();
        Scanner scanner = new Scanner(new File(generationProperties.getDictionaryFile()));
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
