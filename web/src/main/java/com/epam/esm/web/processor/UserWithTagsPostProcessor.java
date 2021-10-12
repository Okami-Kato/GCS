package com.epam.esm.web.processor;

import com.epam.esm.service.dto.response.UserWithTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserWithTagsPostProcessor implements RepresentationModelPostProcessor<UserWithTags> {
    private final UserPostProcessor userPostProcessor;
    private final TagPostProcessor tagPostProcessor;

    @Autowired
    public UserWithTagsPostProcessor(UserPostProcessor userPostProcessor, TagPostProcessor tagPostProcessor) {
        this.userPostProcessor = userPostProcessor;
        this.tagPostProcessor = tagPostProcessor;
    }

    @Override
    public void processEntity(UserWithTags entity) {
        userPostProcessor.processEntity(entity.getUser());
        tagPostProcessor.processCollection(entity.getTags());
    }
}
