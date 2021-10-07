package com.epam.esm.data_generation.creator;

import com.epam.esm.data_generation.properties.TagProperties;
import com.epam.esm.generator.impl.RandomTag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TagCreator {
    Logger logger = LoggerFactory.getLogger(TagCreator.class);

    private final TagService tagService;

    private Map<Integer, List<String>> dictionary;

    @Autowired
    public TagCreator(TagService tagService) {
        this.tagService = tagService;
    }

    @Autowired
    @Qualifier("dictionary")
    public void setDictionary(Map<Integer, List<String>> dictionary) {
        this.dictionary = dictionary;
    }

    public void create(TagProperties properties) {
        long before = tagService.getCount();

        for (int i = 0; i < properties.getAmount(); i++) {
            CreateTagRequest tag = new RandomTag()
                    .withName(properties.getName().getMinSize(), properties.getName().getMaxSize(), dictionary)
                    .generate();
            try {
                tagService.create(tag);
            } catch (ServiceException e) {
                logger.error(e.getMessage());
            }
        }
        long after = tagService.getCount();
        logger.info("Generated {} tags", after - before);
    }
}
