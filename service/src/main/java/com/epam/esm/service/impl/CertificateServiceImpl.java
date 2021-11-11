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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {
    private final ModelMapper mapper;
    private final CertificateDao certificateDao;
    private final TagDao tagDao;

    @Autowired
    public CertificateServiceImpl(CertificateDao certificateDao, TagDao tagDao, ModelMapper mapper) {
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
        this.mapper = mapper;
    }

    /**
     * Retrieves all certificates.
     *
     * @param pageable pagination restrictions.
     * @return page of certificates.
     */
    @Override
    public Page<CertificateItem> findAll(Pageable pageable) {
        return certificateDao.findAll(pageable)
                .map(c -> mapper.map(c, CertificateItem.class));
    }

    /**
     * Retrieves all certificates, that match filter.
     *
     * @param specification certificate restrictions.
     * @param pageable      pagination restrictions.
     * @return page of found certificates.
     */
    @Override
    public Page<CertificateItem> findAll(Specification<Certificate> specification, Pageable pageable) {
        return certificateDao.findAll(specification, pageable)
                .map(c -> mapper.map(c, CertificateItem.class));
    }

    /**
     * Retrieves all certificates, that have tag with given id.
     *
     * @param pageable pagination restrictions.
     * @param tagId    id of tag.
     * @return page of found certificates.
     * @throws EntityNotFoundException  if tag with given tagId wasn't found.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public Page<CertificateItem> findAllByTagId(int tagId, Pageable pageable) {
        if (!tagDao.existsById(tagId)) {
            throw new EntityNotFoundException("id=" + tagId, ErrorCode.TAG_NOT_FOUND);
        }
        return certificateDao.findAllByTagsId(tagId, pageable)
                .map(c -> mapper.map(c, CertificateItem.class));
    }

    /**
     * Retrieves certificate with given id.
     *
     * @param id id of certificate.
     * @return Optional with certificate, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<CertificateResponse> findById(int id) {
        return certificateDao.findById(id).map(o -> mapper.map(o, CertificateResponse.class));
    }

    /**
     * Returns count of certificates.
     *
     * @return count of certificates.
     */
    @Override
    public long getCount() {
        return certificateDao.count();
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
            Certificate created = certificateDao.saveAndFlush(certificateToCreate);
            return mapper.map(created, CertificateResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_CERTIFICATE);
        }
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
            if (!certificateDao.existsById(certificate.getId())) {
                throw new EntityNotFoundException("id=" + certificate.getId(), ErrorCode.CERTIFICATE_NOT_FOUND);
            }
            if (certificate.getTags() != null) {
                extractTags(certificateToUpdate, certificate.getTags());
            }
            Certificate updatedCertificate = certificateDao.saveAndFlush(certificateToUpdate);
            certificateDao.refresh(updatedCertificate);
            return mapper.map(updatedCertificate, CertificateResponse.class);
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
            certificateDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id=" + id, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
    }

    private void extractTags(Certificate certificateToCreate, Set<TagRequest> tagRequests) {
        for (TagRequest tagRequest : tagRequests) {
            Assert.notNull(tagRequest, "Tag can't be null");
            Optional<Tag> tag = tagDao.findByName(tagRequest.getName());
            certificateToCreate.addTag(
                    tag.orElseGet(() -> {
                        Tag tagToCreate = Tag.builder()
                                .name(tagRequest.getName())
                                .build();
                        try {
                            return tagDao.save(tagToCreate);
                        } catch (DataIntegrityViolationException e) {
                            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_TAG);
                        }
                    })
            );
        }
    }
}
