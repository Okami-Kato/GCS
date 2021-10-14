package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import com.epam.esm.util.CertificateFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {
    private ModelMapper mapper;
    private CertificateDao certificateDao;
    private TagDao tagDao;

    @Autowired
    public CertificateServiceImpl(CertificateDao certificateDao, TagDao tagDao, ModelMapper mapper) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
        this.mapper = mapper;
    }

    /**
     * Retrieves all certificates.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of certificates.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<CertificateItem> findAll(int pageNumber, int pageSize) {
        try {
            return certificateDao.findAll(pageNumber, pageSize).stream()
                    .map(c -> mapper.map(c, CertificateItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves all certificates, that match filter.
     *
     * @param filter     filter details.
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of found certificates.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0, or if filter
     *                                  contains invalid sorting properties.
     */
    @Override
    public List<CertificateItem> findAllWithFilter(int pageNumber, int pageSize, CertificateFilter filter) {
        try {
            return certificateDao.findAllWithFilter(pageNumber, pageSize, filter).stream()
                    .map(c -> mapper.map(c, CertificateItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves all certificates, that have tag with given id.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @param tagId      id of tag.
     * @return list of found certificates.
     * @throws EntityNotFoundException  if tag with given tagId wasn't found.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<CertificateItem> findAllByTagId(int pageNumber, int pageSize, int tagId) {
        if (!tagDao.find(tagId).isPresent()) {
            throw new EntityNotFoundException("id=" + tagId, ErrorCode.TAG_NOT_FOUND);
        }
        try {
            return certificateDao.findAllByTagId(pageNumber, pageSize, tagId).stream()
                    .map(c -> mapper.map(c, CertificateItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves certificate with given id.
     *
     * @param id id of certificate.
     * @return Optional with certificate, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<CertificateResponse> find(int id) {
        return certificateDao.find(id).map(o -> mapper.map(o, CertificateResponse.class));
    }

    /**
     * Returns count of certificates.
     *
     * @return count of certificates.
     */
    @Override
    public long getCount() {
        return certificateDao.getCount();
    }

    /**
     * Creates new certificate from given {@link CreateCertificateRequest}.
     *
     * @param certificate certificate to create.
     * @return created certificate.
     * @throws IllegalArgumentException if certificate is null.
     * @throws InvalidEntityException   if certificate, or it's tags are invalid.
     */
    @Override
    public CertificateResponse create(CreateCertificateRequest certificate) {
        Certificate certificateToCreate = mapper.map(certificate, Certificate.class);
        try {
            if (certificate.getTags() != null) {
                extractTags(certificateToCreate, certificate.getTags());
            }
            certificateDao.create(certificateToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_TAG);
        }
        return mapper.map(certificateToCreate, CertificateResponse.class);
    }

    /**
     * Updates certificate. New values are taken from {@link UpdateCertificateRequest} object.
     *
     * @param certificate updated certificate.
     * @return updated certificate.
     * @throws IllegalArgumentException if certificate is null.
     * @throws EntityNotFoundException  if certificate with given id wasn't found.
     * @throws InvalidEntityException   if certificate, or it's tags are invalid.
     */
    @Override
    public CertificateResponse update(UpdateCertificateRequest certificate) {
        Certificate certificateToUpdate = mapper.map(certificate, Certificate.class);
        try {
            if (certificate.getTags() != null) {
                extractTags(certificateToUpdate, certificate.getTags());
            }
            Certificate updatedCertificate = certificateDao.update(certificateToUpdate);
            return mapper.map(updatedCertificate, CertificateResponse.class);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new EntityNotFoundException("id=" + certificate.getId(), ErrorCode.CERTIFICATE_NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_CERTIFICATE);
        }
    }

    /**
     * Deletes certificate with given id.
     *
     * @param id id of certificate to delete.
     * @throws EntityNotFoundException if certificate wasn't found.
     */
    @Override
    public void delete(int id) {
        try {
            certificateDao.delete(id);
        } catch (JpaObjectRetrievalFailureException e) {
            throw new EntityNotFoundException("id=" + id, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
    }

    private void extractTags(Certificate certificateToCreate, Set<TagRequest> tagRequests) {
        for (TagRequest tagRequest : tagRequests) {
            Assert.notNull(tagRequest, "Tag can't be null");
            Optional<Tag> tag = tagDao.get(tagRequest.getName());
            certificateToCreate.addTag(
                    tag.orElseGet(() -> {
                        Tag tagToCreate = new Tag(tagRequest.getName());
                        try {
                            tagDao.create(tagToCreate);
                        } catch (DataIntegrityViolationException e) {
                            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_TAG);
                        }
                        return tagToCreate;
                    })
            );
        }
    }
}
