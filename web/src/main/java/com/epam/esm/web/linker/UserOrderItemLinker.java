package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.web.config.Roles;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserOrderItemLinker implements RepresentationModelLinker<UserOrderItem> {
    private final Supplier<Authentication> authenticationSupplier;

    @Autowired
    public UserOrderItemLinker(Supplier<Authentication> authenticationSupplier) {
        this.authenticationSupplier = authenticationSupplier;
    }

    @Override
    public void processEntity(UserOrderItem entity) {
        entity.add(linkTo(methodOn(UserOrderController.class).findOrder(entity.getId(), null)).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .findAllOrdersByUserId(entity.getUserId(), null))
                .withRel("ordersOfUser"));
        Authentication authentication = authenticationSupplier.get();
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN))) {
            entity.add(linkTo(methodOn(UserOrderController.class)
                    .findAllOrdersByCertificateId(entity.getCertificateId(), null))
                    .withRel("ordersOnCertificate"));
        }
    }
}
