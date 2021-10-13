package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserOrderItemLinker implements RepresentationModelLinker<UserOrderItem> {
    @Override
    public void processEntity(UserOrderItem entity) {
        entity.add(linkTo(methodOn(UserOrderController.class).getOrder(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .getAllOrdersByUserId(null, null, entity.getUserId()))
                .withRel("ordersOfUser"));
        entity.add(linkTo(methodOn(UserOrderController.class)
                .getAllOrdersByCertificateId(null, null, entity.getCertificateId()))
                .withRel("ordersOnCertificate"));
    }
}
