package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserWithTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserWithTagsLinker implements RepresentationModelLinker<UserWithTags> {
    private final TagLinker tagPostProcessor;

    @Autowired
    public UserWithTagsLinker(TagLinker tagPostProcessor) {
        this.tagPostProcessor = tagPostProcessor;
    }

    @Override
    public void processEntity(UserWithTags entity) {
        tagPostProcessor.processCollection(entity.getTags());
    }
}
