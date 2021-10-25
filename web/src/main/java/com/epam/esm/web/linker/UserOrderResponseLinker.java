package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserOrderResponse;
import com.epam.esm.web.controller.UserController;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserOrderResponseLinker implements RepresentationModelLinker<UserOrderResponse> {
    private final CertificateLinker certificateLinker;

    @Autowired
    public UserOrderResponseLinker(CertificateLinker certificateLinker) {
        this.certificateLinker = certificateLinker;
    }

    @Override
    public void processEntity(UserOrderResponse entity) {
        entity.add(linkTo(methodOn(UserOrderController.class).findOrder(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .findAllOrdersByUserId(entity.getUserId(), null))
                .withRel("ordersOfUser"));
        entity.add(linkTo(methodOn(UserController.class)
                .findUser(entity.getUserId()))
                .withRel("user"));
        if (entity.getCertificate() != null) {
            entity.add(linkTo(methodOn(UserOrderController.class)
                    .findAllOrdersByCertificateId(entity.getCertificate().getId(), null))
                    .withRel("ordersOnCertificate"));
            certificateLinker.processEntity(entity.getCertificate());
        }
    }
}
