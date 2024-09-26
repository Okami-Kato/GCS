package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserWithTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserWithTagsLinker implements RepresentationModelLinker<UserWithTags> {
    private final UserLinker userPostProcessor;
    private final TagLinker tagPostProcessor;

    @Autowired
    public UserWithTagsLinker(UserLinker userPostProcessor, TagLinker tagPostProcessor) {
        this.userPostProcessor = userPostProcessor;
        this.tagPostProcessor = tagPostProcessor;
    }

    @Override
    public void processEntity(UserWithTags entity) {
        userPostProcessor.processEntity(entity.getUser());
        tagPostProcessor.processCollection(entity.getTags());
    }
}
