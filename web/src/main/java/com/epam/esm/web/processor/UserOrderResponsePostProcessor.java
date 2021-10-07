package com.epam.esm.web.processor;

import com.epam.esm.service.dto.response.UserOrderResponse;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserOrderResponsePostProcessor implements RepresentationModelPostProcessor<UserOrderResponse> {
    private final CertificatePostProcessor certificatePostProcessor;
    private final UserPostProcessor userPostProcessor;

    @Autowired
    public UserOrderResponsePostProcessor(CertificatePostProcessor certificatePostProcessor, UserPostProcessor userPostProcessor) {
        this.certificatePostProcessor = certificatePostProcessor;
        this.userPostProcessor = userPostProcessor;
    }

    @Override
    public void processEntity(UserOrderResponse entity) {
        entity.add(linkTo(methodOn(UserOrderController.class).getOrder(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .getAllOrdersByUserId(null, null, entity.getUser().getId()))
                .withRel("ordersOfUser"));
        userPostProcessor.processEntity(entity.getUser());
        if (entity.getCertificate() != null) {
            entity.add(linkTo(methodOn(UserOrderController.class)
                    .getAllOrdersByCertificateId(null, null, entity.getCertificate().getId()))
                    .withRel("ordersOnCertificate"));
            certificatePostProcessor.processEntity(entity.getCertificate());
        }
    }
}
