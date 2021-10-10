package com.epam.esm;

import com.epam.esm.creator.CertificateCreator;
import com.epam.esm.creator.TagCreator;
import com.epam.esm.creator.UserCreator;
import com.epam.esm.creator.UserOrderCreator;
import com.epam.esm.properties.GenerationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

@SpringBootApplication
@EnableConfigurationProperties(GenerationProperties.class)
public class GenerationApplication {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(GenerationApplication.class, args)) {
            GenerationProperties properties = context.getBean(GenerationProperties.class);

            TagCreator tagCreator = context.getBean(TagCreator.class);
            tagCreator.create(properties.getTag().getAmount());

            CertificateCreator certificateCreator = context.getBean(CertificateCreator.class);
            certificateCreator.create(properties.getCertificate().getAmount());

            UserCreator userCreator = context.getBean(UserCreator.class);
            userCreator.create(properties.getUser().getAmount());

            UserOrderCreator userOrderCreator = context.getBean(UserOrderCreator.class);
            userOrderCreator.create(properties.getUserOrder().getAmount());
        }
    }

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
