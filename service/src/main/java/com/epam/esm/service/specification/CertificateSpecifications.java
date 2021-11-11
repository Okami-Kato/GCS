package com.epam.esm.service.specification;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Certificate_;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.Tag_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;

public class CertificateSpecifications {
    public static Specification<Certificate> nameLike(String name) {
        return (root, query, builder) -> builder.like(root.get(Certificate_.name), "%" + name + "%");
    }

    public static Specification<Certificate> descriptionLike(String description) {
        return (root, query, builder) -> builder.like(root.get(Certificate_.description), "%" + description + "%");
    }

    public static Specification<Certificate> withTags(String... tagNames) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            SetJoin<Certificate, Tag> tags = root.join(Certificate_.tags);
            predicate.getExpressions().add(tags.get(Tag_.name).in((Object[]) tagNames));
            query.having(builder.equal(builder.count(tags.get(Tag_.name)), tagNames.length));
            query.groupBy(root.get(Certificate_.id));
            return predicate;
        };
    }
}
