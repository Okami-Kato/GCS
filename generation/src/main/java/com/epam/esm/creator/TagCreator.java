package com.epam.esm.creator;

import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TagCreator implements Creator {
    Logger logger = LoggerFactory.getLogger(TagCreator.class);
    private final TagService tagService;

    public TagCreator(TagService tagService) {
        this.tagService = tagService;
    }

    protected abstract CreateTagRequest getTag();

    @Override
    public int create(int amount) {
        int counter = 0;
        for (int i = 0; i < amount; i++) {
            try {
                CreateTagRequest tag;
                do
                    tag = getTag();
                while (tagService.get(tag.getName()).isPresent());
                tagService.create(tag);
                counter++;
            } catch (ServiceException e) {
                logger.error(e.getMessage());
            }
        }
        return counter;
    }
}
