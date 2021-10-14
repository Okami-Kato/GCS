package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.dto.response.UserWithTags;
import com.epam.esm.service.exception.EntityExistsException;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private final ModelMapper mapper;
    private final TagDao tagDao;
    private final CertificateDao certificateDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao, CertificateDao certificateDao, ModelMapper mapper) {
        this.tagDao = tagDao;
        this.certificateDao = certificateDao;
        this.mapper = mapper;
    }

    /**
     * Retrieves all tags.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of tags.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<TagResponse> findAll(int pageNumber, int pageSize) {
        try {
            return tagDao.findAll(pageNumber, pageSize).stream()
                    .map(tag -> mapper.map(tag, TagResponse.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves all tags, assigned to given certificate.
     *
     * @param pageNumber    number of page (starts from 1).
     * @param pageSize      size of page.
     * @param certificateId id of certificate.
     * @return list of found tags.
     * @throws EntityNotFoundException  if certificate with given id wasn't found
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<TagResponse> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        if (!certificateDao.find(certificateId).isPresent()) {
            throw new EntityNotFoundException("id=" + certificateId, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
        try {
            return tagDao.findAllByCertificateId(pageNumber, pageSize, certificateId).stream()
                    .map(tag -> mapper.map(tag, TagResponse.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves tag with given id.
     *
     * @param id id of tag.
     * @return Optional with tag, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<TagResponse> find(int id) {
        return tagDao.find(id).map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves tag with given name.
     *
     * @param name name of desired tag.
     * @return {@link Optional} of found tag.
     * @throws IllegalArgumentException if name is null.
     */
    @Override
    public Optional<TagResponse> find(String name) {
        Assert.notNull(name, "Tag name can't be null");
        return tagDao.get(name).map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves the most widely used tags of users with the highest cost of all orders.
     *
     * @return found users and corresponding tags.
     */
    @Override
    public List<UserWithTags> findTheMostUsedTagsOfUsersWithTheHighestCost() {
        List<UserWithTags> result = new LinkedList<>();
        tagDao.getTheMostUsedTagsOfUsersWithTheHighestCost().forEach(((user, tags) ->
                result.add(new UserWithTags(
                        mapper.map(user, UserResponse.class),
                        tags.stream()
                                .map(tag -> mapper.map(tag, TagResponse.class))
                                .collect(Collectors.toList())))));
        return result;
    }

    /**
     * Returns count of tags.
     *
     * @return count of tags.
     */
    @Override
    public long getCount() {
        return tagDao.getCount();
    }

    /**
     * Creates new tag from given {@link TagRequest}.
     *
     * @param tag tag create.
     * @return created tag.
     * @throws IllegalArgumentException if tag is null.
     * @throws InvalidEntityException   if tag is invalid.
     * @throws EntityExistsException    if tag with the same name already exists.
     */
    @Override
    public TagResponse create(TagRequest tag) {
        Tag tagToCreate = mapper.map(tag, Tag.class);
        if (tagDao.get(tagToCreate.getName()).isPresent()) {
            throw new EntityExistsException("name=" + tag.getName(), ErrorCode.TAG_EXISTS);
        }
        try {
            tagDao.create(tagToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_TAG);
        }
        return mapper.map(tagToCreate, TagResponse.class);
    }

    /**
     * Deletes tag with given id.
     *
     * @param id id of tag to delete.
     * @throws EntityNotFoundException if tag wasn't found.
     */
    @Override
    public void delete(int id) {
        try {
            tagDao.delete(id);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new EntityNotFoundException("id=" + id, ErrorCode.TAG_NOT_FOUND);
        }
    }
}
