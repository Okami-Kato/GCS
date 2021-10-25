package com.epam.esm.service;

import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Page<TagResponse> findAll(Pageable pageable);

    Page<TagResponse> findAllByCertificateId(int certificateId, Pageable pageable);

    Optional<TagResponse> findById(int id);

    Optional<TagResponse> findByName(String name);

    List<TagResponse> findTheMostUsedTagsOfUser(int userId);

    long getCount();

    TagResponse create(TagRequest tag);

    void delete(int id);
}
