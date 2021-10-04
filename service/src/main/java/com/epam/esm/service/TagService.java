package com.epam.esm.service;

import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserWithTags;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagService {
    List<TagResponse> getAll(int pageNumber, int pageSize);

    List<TagResponse> getAll(int pageNumber, int pageSize, int certificateId);

    Optional<TagResponse> get(int id);

    Optional<TagResponse> get(String name);

    List<UserWithTags> getTheMostUsedTagsOfUsersWithTheHighestCost();

    long getCount();

    TagResponse create(CreateTagRequest tag);

    void delete(int id);
}
