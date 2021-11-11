package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.EntityExistsException;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
     * @param pageable pagination restrictions.
     * @return page of tags.
     */
    @Override
    public Page<TagResponse> findAll(Pageable pageable) {
        return tagDao.findAll(pageable)
                .map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves all tags, assigned to given certificate.
     *
     * @param pageable      pagination restrictions.
     * @param certificateId id of certificate.
     * @return page of found tags.
     * @throws EntityNotFoundException if certificate with given id wasn't found
     */
    @Override
    public Page<TagResponse> findAllByCertificateId(int certificateId, Pageable pageable) {
        if (!certificateDao.existsById(certificateId)) {
            throw new EntityNotFoundException("id=" + certificateId, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
        return tagDao.findAllByCertificatesId(certificateId, pageable)
                .map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves tag with given id.
     *
     * @param id id of tag.
     * @return Optional with tag, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<TagResponse> findById(int id) {
        return tagDao.findById(id).map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves tag with given name.
     *
     * @param name name of desired tag.
     * @return {@link Optional} of found tag.
     * @throws IllegalArgumentException if name is null.
     */
    @Override
    public Optional<TagResponse> findByName(String name) {
        Assert.notNull(name, "Tag name can't be null");
        return tagDao.findByName(name).map(tag -> mapper.map(tag, TagResponse.class));
    }

    /**
     * Retrieves the most widely used tags of users with the highest cost of all orders.
     *
     * @return found users and corresponding tags.
     */
    @Override
    public List<TagResponse> findTheMostUsedTagsOfUser(String userId) {
        return tagDao.findTheMostUsedTagsOfUser(userId).stream()
                .map(tag -> mapper.map(tag, TagResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns count of tags.
     *
     * @return count of tags.
     */
    @Override
    public long getCount() {
        return tagDao.count();
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
        if (tagDao.existsByName(tag.getName())) {
            throw new EntityExistsException("name=" + tag.getName(), ErrorCode.TAG_EXISTS);
        }
        try {
            Tag created = tagDao.save(tagToCreate);
            return mapper.map(created, TagResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_TAG);
        }
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
            tagDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id=" + id, ErrorCode.TAG_NOT_FOUND);
        }
    }
}
