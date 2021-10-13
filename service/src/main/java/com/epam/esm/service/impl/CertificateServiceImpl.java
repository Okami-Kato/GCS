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
import com.epam.esm.util.CertificateFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epam.esm.service.util.ServiceUtil.executeDaoCall;

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
        return executeDaoCall(() -> certificateDao.getAll(pageNumber, pageSize).stream()
                .map(c -> mapper.map(c, CertificateItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<CertificateItem> findAllWithFilter(int pageNumber, int pageSize, CertificateFilter certificateFilter) {
        return executeDaoCall(() -> certificateDao.findAllWithFilter(pageNumber, pageSize, certificateFilter).stream()
                .map(c -> mapper.map(c, CertificateItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<CertificateItem> findAllWithByTagId(int pageNumber, int pageSize, int tagId) {
        return executeDaoCall(() -> certificateDao.findAllByTagId(pageNumber, pageSize, tagId).stream()
                .map(c -> mapper.map(c, CertificateItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<CertificateResponse> get(int id) {
        return executeDaoCall(() -> certificateDao.get(id).map(o -> mapper.map(o, CertificateResponse.class)));
    }

    @Override
    public long getCount() {
        return certificateDao.getCount();
    }

    @Override
    public CertificateResponse create(CreateCertificateRequest certificate) {
        Certificate certificateToCreate = mapper.map(certificate, Certificate.class);
        if (certificate.getTags() != null) {
            extractTags(certificateToCreate, certificate.getTags());
        }
        executeDaoCall(() -> certificateDao.create(certificateToCreate));
        return mapper.map(certificateToCreate, CertificateResponse.class);
    }

    @Override
    public CertificateResponse update(UpdateCertificateRequest certificate) {
        Certificate certificateToUpdate = mapper.map(certificate, Certificate.class);
        if (certificate.getTags() != null) {
            extractTags(certificateToUpdate, certificate.getTags());
        }
        Certificate updatedCertificate = executeDaoCall(() -> certificateDao.update(certificateToUpdate));
        return mapper.map(updatedCertificate, CertificateResponse.class);
    }

    @Override
    public void delete(int id) {
        executeDaoCall(() -> certificateDao.delete(id));
    }

    private void extractTags(Certificate certificateToCreate, Set<TagRequest> tagRequests) {
        for (TagRequest tagRequest : tagRequests) {
            if (tagRequest == null) {
                throw new IllegalArgumentException("Tag can't be null");
            }
            Optional<Tag> tag = tagDao.get(tagRequest.getName());
            certificateToCreate.addTag(
                    tag.orElseGet(() -> {
                        Tag tagToCreate = new Tag(tagRequest.getName());
                        executeDaoCall(() -> tagDao.create(tagToCreate));
                        return tagToCreate;
                    })
            );
        }
    }
}
