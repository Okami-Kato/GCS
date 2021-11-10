package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.web.config.Roles;
import com.epam.esm.web.controller.CertificateController;
import com.epam.esm.web.controller.UserOrderController;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateLinker implements RepresentationModelLinker<CertificateItem> {
    private final TagLinker tagPostProcessor;
    private final Supplier<Authentication> authSupplier;

    @Autowired
    public CertificateLinker(TagLinker tagPostProcessor, Supplier<Authentication> authSupplier) {
        this.tagPostProcessor = tagPostProcessor;
        this.authSupplier = authSupplier;
    }

    @Override
    @SneakyThrows
    public void processEntity(CertificateItem entity) {
        entity.add(linkTo(methodOn(CertificateController.class).findCertificate(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(CertificateController.class)
                .findAllCertificates(null, null, null, null)).withRel("allCertificates"));
        Authentication authentication = authSupplier.get();
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN))) {
            entity.add(linkTo(methodOn(CertificateController.class).updateCertificate(entity.getId(), null)).withRel("update"));
            entity.add(linkTo(methodOn(CertificateController.class).deleteCertificate(entity.getId())).withRel("delete"));
            entity.add(linkTo(methodOn(UserOrderController.class)
                    .findAllOrdersByCertificateId(entity.getId(), null))
                    .withRel("ordersOnCertificate"));
        }
        entity.getTags().forEach(tagPostProcessor::processEntity);
    }
}
