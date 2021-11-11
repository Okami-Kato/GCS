package com.epam.esm.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;

@Relation(collectionRelation = "certificates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CertificateItem extends RepresentationModel<CertificateItem> {
    protected Integer id;
    protected String name;
    protected Integer price;

    protected Set<TagResponse> tags;
}
