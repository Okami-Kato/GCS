package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class CertificateDaoTest {
    private final Tag firstTag = Tag.builder().name("first tag").build();
    private final Tag secondTag = Tag.builder().name("second tag").build();

    private final Certificate firstCertificate = Certificate.builder()
            .name("first certificate")
            .description("first description")
            .price(1)
            .duration(3)
            .tags(Sets.newHashSet(firstTag, secondTag))
            .build();
    private final Certificate secondCertificate = Certificate.builder()
            .name("second certificate")
            .description("second description")
            .price(2)
            .duration(5)
            .tags(Sets.newHashSet(firstTag))
            .build();

    private final Certificate thirdCertificate = Certificate.builder()
            .name("third certificate")
            .description("third description")
            .price(2)
            .duration(7)
            .tags(Sets.newHashSet(secondTag))
            .build();

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;

    @BeforeAll
    void init() {
        tagDao.saveAll(Arrays.asList(firstTag, secondTag));
    }

    @AfterAll
    void destroy() {
        tagDao.deleteAllByIdInBatch(Arrays.asList(firstTag.getId(), secondTag.getId()));
    }

    @BeforeEach
    void createAll() {
        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate));
    }

    @AfterEach
    void deleteAll() {
        certificateDao.deleteAllInBatch(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate));
        firstCertificate.setId(null);
        secondCertificate.setId(null);
        thirdCertificate.setId(null);
    }

    @Test
    void create() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.save(null));
        Optional<Certificate> persisted = certificateDao.findById(firstCertificate.getId());
        assertTrue(persisted.isPresent());
        assertTrue(persisted.get().getTags().containsAll(Arrays.asList(firstTag, secondTag)));
        assertFalse(certificateDao.findById(firstCertificate.getId() + 1000).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.findById(null));
    }

    @Test
    void update() {
        Certificate certificate = certificateDao.findById(thirdCertificate.getId()).get();
        certificate.setName("new name");
        certificate.setDescription("new description");
        certificate.addTag(firstTag);
        assertDoesNotThrow(() -> certificateDao.save(certificate));
        Optional<Certificate> persisted = certificateDao.findById(thirdCertificate.getId());
        assertTrue(persisted.isPresent());
        assertNotNull(persisted.get().getLastUpdateDate());
        assertEquals(certificate.getName(), persisted.get().getName());
        assertEquals(certificate.getDescription(), persisted.get().getDescription());
        assertEquals(certificate.getTags(), persisted.get().getTags());

        certificate.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> certificateDao.save(certificate));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.save(null));
    }

    @Test
    void createAndDelete() {
        Certificate certificate = Certificate.builder()
                .name("certificate")
                .description("description")
                .price(1)
                .duration(4)
                .build();
        assertDoesNotThrow(() -> certificateDao.save(certificate));
        Optional<Certificate> persisted = certificateDao.findById(certificate.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> certificateDao.deleteById(certificate.getId()));
        assertFalse(certificateDao.findById(certificate.getId()).isPresent());
        assertThrows(EmptyResultDataAccessException.class, () -> certificateDao.deleteById(certificate.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.deleteById(null));
    }
}