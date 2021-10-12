package com.epam.esm.web.processor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;

public interface RepresentationModelPostProcessor<T extends RepresentationModel<? extends T>> {
    void processEntity(T entity);

    default CollectionModel<? extends T> processCollection(Iterable<? extends T> content) {
        content.forEach(this::processEntity);
        return CollectionModel.of(content);
    }
}
