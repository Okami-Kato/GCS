package com.epam.esm.web.processor;

import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.web.controller.TagController;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagPostProcessor implements RepresentationModelPostProcessor<TagResponse> {
    @Override
    public void processEntity(TagResponse entity) {
        entity.add(linkTo(methodOn(TagController.class).getTag(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(TagController.class).deleteTag(entity.getId())).withRel("delete"));
        entity.add(linkTo(methodOn(TagController.class).getCertificates(null, null, entity.getId())).withRel("certificatesAssignedToTag"));
        entity.add(linkTo(methodOn(TagController.class).getAllTags(null, null)).withRel("allTags"));
    }
}
