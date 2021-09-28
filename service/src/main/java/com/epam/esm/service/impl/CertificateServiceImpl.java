package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.CertificateFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<CertificateItem> getAll(int pageNumber, int pageSize) {
        try {
            return certificateDao.getAll(pageNumber, pageSize).stream()
                    .map(c -> mapper.map(c, CertificateItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<CertificateItem> getAll(int pageNumber, int pageSize, CertificateFilter certificateFilter) {
        try {
            return certificateDao.getAll(pageNumber, pageSize, certificateFilter).stream()
                    .map(c -> mapper.map(c, CertificateItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<CertificateResponse> get(int id) {
        try {
            return certificateDao.get(id).map(o -> mapper.map(o, CertificateResponse.class));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public long getCount() {
        return certificateDao.getCount();
    }

    @Override
    public CertificateResponse create(CreateCertificateRequest certificate) {
        Certificate certificateToCreate = mapper.map(certificate, Certificate.class);
        if (certificate.getTagNames() != null) {
            extractTags(certificateToCreate, certificate.getTagNames());
        }
        try {
            certificateDao.create(certificateToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(e);
        }
        return mapper.map(certificateToCreate, CertificateResponse.class);
    }

    @Override
    public CertificateResponse update(UpdateCertificateRequest certificate) {
        Certificate certificateToUpdate = mapper.map(certificate, Certificate.class);
        if (certificate.getTagNames() != null) {
            extractTags(certificateToUpdate, certificate.getTagNames());
        }
        try {
            certificateDao.update(certificateToUpdate);
        } catch (DataIntegrityViolationException | InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
        return mapper.map(certificateToUpdate, CertificateResponse.class);
    }

    @Override
    public void delete(int id) {
        try {
            certificateDao.delete(id);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    private void extractTags(Certificate certificateToCreate, Set<String> tagNames) {
        for (String tagName : tagNames) {
            if (tagName == null) {
                throw new IllegalArgumentException("Tag name can't be null");
            }
            Optional<Tag> tag = tagDao.get(tagName);
            if (tag.isPresent()) {
                certificateToCreate.addTag(tag.get());
            } else {
                Tag tagToCreate = new Tag(tagName);
                tagDao.create(tagToCreate);
                certificateToCreate.addTag(tagToCreate);
            }
        }
    }
}
