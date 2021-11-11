package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.web.config.Roles;
import com.epam.esm.web.controller.TagController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagLinker implements RepresentationModelLinker<TagResponse> {
    private final Supplier<Authentication> authenticationSupplier;

    @Autowired
    public TagLinker(Supplier<Authentication> authenticationSupplier) {
        this.authenticationSupplier = authenticationSupplier;
    }

    @Override
    public void processEntity(TagResponse entity) {
        entity.add(linkTo(methodOn(TagController.class).findTag(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(TagController.class).findCertificates(null, entity.getId())).withRel("certificatesAssignedToTag"));
        entity.add(linkTo(methodOn(TagController.class).findAllTags(null)).withRel("allTags"));
        Authentication authentication = authenticationSupplier.get();
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN))) {
            entity.add(linkTo(methodOn(TagController.class).deleteTag(entity.getId())).withRel("delete"));
        }
    }
}
