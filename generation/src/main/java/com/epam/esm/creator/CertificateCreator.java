package com.epam.esm.creator;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CertificateCreator implements Creator {
    Logger logger = LoggerFactory.getLogger(CertificateCreator.class);
    private final CertificateService certificateService;

    public CertificateCreator(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    protected abstract CreateCertificateRequest getCertificate();

    @Override
    public void create(int amount) {
        for (int i = 0; i < amount; i++) {
            try {
                certificateService.create(getCertificate());
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                i--;
            }
        }
    }
}
