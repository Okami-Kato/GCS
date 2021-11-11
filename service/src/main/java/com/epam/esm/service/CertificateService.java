package com.epam.esm.service;

import com.epam.esm.entity.Certificate;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface CertificateService {
    Page<CertificateItem> findAll(Pageable pageable);

    Page<CertificateItem> findAll(Specification<Certificate> specification, Pageable pageable);

    Page<CertificateItem> findAllByTagId(int tagId, Pageable pageable);

    Optional<CertificateResponse> findById(int id);

    long getCount();

    CertificateResponse create(CreateCertificateRequest certificate);

    CertificateResponse update(UpdateCertificateRequest certificate);

    void delete(int id);
}
