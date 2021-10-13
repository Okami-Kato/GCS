package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.dto.response.UserWithTags;
import com.epam.esm.service.exception.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.service.util.ServiceUtil.executeDaoCall;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private ModelMapper mapper;
    private TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao, ModelMapper mapper) {
        this.tagDao = tagDao;
        this.mapper = mapper;
    }

    @Override
    public List<TagResponse> getAll(int pageNumber, int pageSize) {
        return executeDaoCall(() -> tagDao.getAll(pageNumber, pageSize).stream()
                .map(tag -> mapper.map(tag, TagResponse.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<TagResponse> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        return executeDaoCall(() -> tagDao.findAllByCertificateId(pageNumber, pageSize, certificateId).stream()
                .map(tag -> mapper.map(tag, TagResponse.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<TagResponse> get(int id) {
        return tagDao.get(id).map(tag -> mapper.map(tag, TagResponse.class));
    }

    @Override
    public Optional<TagResponse> get(String name) {
        return executeDaoCall(() -> tagDao.get(name).map(tag -> mapper.map(tag, TagResponse.class)));
    }

    @Override
    public List<UserWithTags> getTheMostUsedTagsOfUsersWithTheHighestCost() {
        List<UserWithTags> result = new LinkedList<>();
        tagDao.getTheMostUsedTagsOfUsersWithTheHighestCost().forEach(((user, tags) ->
                result.add(new UserWithTags(
                        mapper.map(user, UserResponse.class),
                        tags.stream()
                                .map(tag -> mapper.map(tag, TagResponse.class))
                                .collect(Collectors.toList())))));
        return result;
    }

    @Override
    public long getCount() {
        return tagDao.getCount();
    }

    @Override
    public TagResponse create(CreateTagRequest tag) {
        Tag tagToCreate = mapper.map(tag, Tag.class);
        try {
            tagDao.create(tagToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(String.format("Tag with name (%s) already exists", tagToCreate.getName()));
        }
        return mapper.map(tagToCreate, TagResponse.class);
    }

    @Override
    public void delete(int id) {
        executeDaoCall(() -> tagDao.delete(id));
    }
}
