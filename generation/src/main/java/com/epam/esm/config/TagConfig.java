package com.epam.esm.config;

import com.epam.esm.creator.TagCreator;
import com.epam.esm.properties.GenerationProperties;
import com.epam.esm.random.primitive.RandomSentence;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class TagConfig {
    private CreateTagRequest generateRandomTag(GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new CreateTagRequest(
                new RandomSentence(dictionary,
                        properties.getTag().getName().getMinSize(),
                        properties.getTag().getName().getMaxSize()
                ).getValue()
        );
    }

    @Bean
    public TagCreator tagCreator(TagService tagService, GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new TagCreator(tagService) {
            @Override
            protected CreateTagRequest getTag() {
                return generateRandomTag(properties, dictionary);
            }
        };
    }
}
