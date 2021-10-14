package com.epam.esm.config;

import com.epam.esm.creator.TagCreator;
import com.epam.esm.properties.GenerationProperties;
import com.epam.esm.random.primitive.RandomSentence;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.TagRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class TagConfig {
    @Bean
    public TagCreator tagCreator(TagService tagService, GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new TagCreator(tagService) {
            @Override
            protected TagRequest getTag() {
                return generateRandomTag(properties, dictionary);
            }
        };
    }

    private TagRequest generateRandomTag(GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new TagRequest(
                new RandomSentence(dictionary,
                        properties.getTag().getName().getMinSize(),
                        properties.getTag().getName().getMaxSize()
                ).getValue()
        );
    }
}
