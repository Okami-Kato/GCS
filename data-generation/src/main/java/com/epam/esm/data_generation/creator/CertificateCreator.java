package com.epam.esm.data_generation.creator;

import com.epam.esm.data_generation.properties.CertificateProperties;
import com.epam.esm.generator.impl.RandomCertificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CertificateCreator {
    Logger logger = LoggerFactory.getLogger(CertificateCreator.class);
    private final CertificateService certificateService;
    private final Map<Integer, List<String>> dictionary;

    @Autowired
    public CertificateCreator(CertificateService certificateService, Map<Integer, List<String>> dictionary) {
        this.certificateService = certificateService;
        this.dictionary = dictionary;
    }

    public void create(CertificateProperties properties, List<String> availableTagNames) {
        long before = certificateService.getCount();
        for (int i = 0; i < properties.getAmount(); i++) {
            CreateCertificateRequest certificate = new RandomCertificate()
                    .withName(properties.getName().getMinSize(), properties.getName().getMaxSize(), dictionary)
                    .withDescription(properties.getDescription().getMinSize(), properties.getDescription().getMaxSize(), dictionary)
                    .withPrice(properties.getPrice().getMin(), properties.getPrice().getMax())
                    .withDuration(properties.getDuration().getMin(), properties.getDuration().getMax())
                    .withTagAmount(properties.getTagAmount().getMin(), properties.getTagAmount().getMax(), availableTagNames)
                    .generate();
            try {
                certificateService.create(certificate);
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                i--;
            }
        }
        long after = certificateService.getCount();
        logger.info("Generated {} certificates", after - before);
    }
}
