package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.web.controller.CertificateController;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateLinker implements RepresentationModelLinker<CertificateItem> {
    private final TagLinker tagPostProcessor;

    @Autowired
    public CertificateLinker(TagLinker tagPostProcessor) {
        this.tagPostProcessor = tagPostProcessor;
    }

    @Override
    public void processEntity(CertificateItem entity) {
        entity.add(linkTo(methodOn(CertificateController.class).getCertificate(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(CertificateController.class).updateCertificate(entity.getId(), null)).withRel("update"));
        entity.add(linkTo(methodOn(CertificateController.class).deleteCertificate(entity.getId())).withRel("delete"));
        entity.add(linkTo(methodOn(UserOrderController.class)
                .getAllOrdersByCertificateId(null, null, entity.getId()))
                .withRel("ordersOnCertificate"));
        entity.add(linkTo(methodOn(CertificateController.class)
                .getAllCertificates(null, null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()))
                .withRel("allCertificates"));
        entity.getTags().forEach(tagPostProcessor::processEntity);
    }
}
