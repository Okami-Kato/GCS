package com.epam.esm.service;

import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserWithTags;

import java.util.List;
import java.util.Optional;

public interface TagService {
    List<TagResponse> findAll(int pageNumber, int pageSize);

    List<TagResponse> findAllByCertificateId(int pageNumber, int pageSize, int certificateId);

    Optional<TagResponse> find(int id);

    Optional<TagResponse> find(String name);

    List<UserWithTags> findTheMostUsedTagsOfUsersWithTheHighestCost();

    long getCount();

    TagResponse create(TagRequest tag);

    void delete(int id);
}
