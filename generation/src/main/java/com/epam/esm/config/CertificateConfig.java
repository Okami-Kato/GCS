package com.epam.esm.config;

import com.epam.esm.creator.CertificateCreator;
import com.epam.esm.properties.CertificateProperties;
import com.epam.esm.properties.GenerationProperties;
import com.epam.esm.random.collection.RandomSubList;
import com.epam.esm.random.primitive.RandomInteger;
import com.epam.esm.random.primitive.RandomSentence;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.response.TagResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class CertificateConfig {
    private CreateCertificateRequest generateRandomCertificate(GenerationProperties properties, Map<Integer, List<String>> dictionary,
                                                               Set<String> availableTagNames) {
        CertificateProperties certificateProperties = properties.getCertificate();
        CreateCertificateRequest certificate = new CreateCertificateRequest();
        certificate.setName(
                new RandomSentence(dictionary,
                        certificateProperties.getName().getMinSize(),
                        certificateProperties.getName().getMaxSize()
                ).getValue()
        );
        certificate.setDescription(
                new RandomSentence(dictionary,
                        certificateProperties.getDescription().getMinSize(),
                        certificateProperties.getDescription().getMaxSize()
                ).getValue()
        );
        certificate.setPrice(
                new RandomInteger(
                        certificateProperties.getPrice().getMin(),
                        certificateProperties.getPrice().getMax()
                ).getValue()
        );
        certificate.setDuration(
                new RandomInteger(
                        certificateProperties.getDuration().getMin(),
                        certificateProperties.getDuration().getMax()
                ).getValue()
        );
        certificate.setTagNames(new HashSet<>(
                new RandomSubList<>(availableTagNames,
                        certificateProperties.getTagAmount().getMin(),
                        certificateProperties.getTagAmount().getMax()
                ).getValue())
        );
        return certificate;
    }

    private Set<String> getAvailableTagNames(TagService tagService) {
        return tagService.getAll(1, Math.toIntExact(tagService.getCount()))
                .stream()
                .map(TagResponse::getName)
                .collect(Collectors.toSet());
    }

    @Bean
    public CertificateCreator certificateCreator(CertificateService certificateService, TagService tagService,
                                                 GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new CertificateCreator(certificateService) {
            @Override
            protected CreateCertificateRequest getCertificate() {
                return generateRandomCertificate(properties, dictionary, getAvailableTagNames(tagService));
            }
        };
    }
}
