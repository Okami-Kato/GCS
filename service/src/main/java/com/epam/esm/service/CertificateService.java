package com.epam.esm.service;

import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.util.CertificateFilter;

import java.util.List;
import java.util.Optional;

public interface CertificateService {
    List<CertificateItem> getAll(int pageNumber, int pageSize);

    List<CertificateItem> getAll(int pageNumber, int pageSize, CertificateFilter certificateFilter);

    Optional<CertificateResponse> get(Integer id);

    long getCount();

    CertificateResponse create(CreateCertificateRequest certificate);

    CertificateResponse update(UpdateCertificateRequest certificate);

    void delete(Integer id);
}
