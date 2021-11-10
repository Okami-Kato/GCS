package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserOrderResponse;
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
public class UserOrderResponseLinker implements RepresentationModelLinker<UserOrderResponse> {
    private final CertificateLinker certificateLinker;
    private final Supplier<Authentication> authenticationSupplier;

    @Autowired
    public UserOrderResponseLinker(CertificateLinker certificateLinker, Supplier<Authentication> authenticationSupplier) {
        this.certificateLinker = certificateLinker;
        this.authenticationSupplier = authenticationSupplier;
    }

    @Override
    public void processEntity(UserOrderResponse entity) {
        entity.add(linkTo(methodOn(UserOrderController.class).findOrder(entity.getId(), null)).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .findAllOrdersByUserId(entity.getUserId(), null))
                .withRel("ordersOfUser"));
        Authentication authentication = authenticationSupplier.get();
        if (entity.getCertificate() != null && authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN))) {
            entity.add(linkTo(methodOn(UserOrderController.class)
                    .findAllOrdersByCertificateId(entity.getCertificate().getId(), null))
                    .withRel("ordersOnCertificate"));
        }
        certificateLinker.processEntity(entity.getCertificate());
    }
}
