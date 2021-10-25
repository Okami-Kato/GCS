package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.dao.specification.CertificateSpecifications.descriptionLike;
import static com.epam.esm.dao.specification.CertificateSpecifications.nameLike;
import static com.epam.esm.dao.specification.CertificateSpecifications.withTags;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class CertificateDaoImplTest {
    private final Certificate firstCertificate = new Certificate(
            "first certificate", "first description", 1, 3);
    private final Certificate secondCertificate = new Certificate(
            "second certificate", "second description", 2, 5);
    private final Certificate thirdCertificate = new Certificate(
            "third certificate", "third description", 2, 7);

    private final Tag firstTag = new Tag("first tag");
    private final Tag secondTag = new Tag("second tag");

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;

    @BeforeAll
    void init() {
        tagDao.saveAll(Arrays.asList(firstTag, secondTag));
        firstCertificate.addTag(firstTag);
        firstCertificate.addTag(secondTag);
        secondCertificate.addTag(firstTag);
        thirdCertificate.addTag(secondTag);
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
        Certificate certificate = new Certificate("certificate", "description", 1, 3);
        assertDoesNotThrow(() -> certificateDao.save(certificate));
        Optional<Certificate> persisted = certificateDao.findById(certificate.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> certificateDao.deleteById(certificate.getId()));
        assertFalse(certificateDao.findById(certificate.getId()).isPresent());
        assertThrows(EmptyResultDataAccessException.class, () -> certificateDao.deleteById(certificate.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.deleteById(null));
    }

    @Test
    void read() {
        assertThrows(IllegalArgumentException.class, () -> certificateDao.findAll(PageRequest.of(0, -10)));
        assertThrows(IllegalArgumentException.class, () -> certificateDao.findAll(PageRequest.of(-10, 1)));

        assertEquals(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate),
                certificateDao.findAll(nameLike("certificate")));
        assertEquals(Collections.singletonList(firstCertificate), certificateDao.findAll(nameLike("first")));
        assertEquals(Collections.emptyList(), certificateDao.findAll(nameLike("certificatee")));

        assertEquals(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate),
                certificateDao.findAll(descriptionLike("description")));
        assertEquals(Collections.singletonList(firstCertificate), certificateDao.findAll(descriptionLike("first")));
        assertEquals(Collections.emptyList(), certificateDao.findAll(descriptionLike("descriptionn")));

        List<Certificate> certificatesWithFirstTag = certificateDao.findAll(withTags(firstTag.getName()));
        assertEquals(Arrays.asList(firstCertificate, secondCertificate), certificatesWithFirstTag);

        List<Certificate> certificatesWithSecondTag = certificateDao.findAll(withTags(secondTag.getName()));
        assertEquals(Arrays.asList(firstCertificate, thirdCertificate), certificatesWithSecondTag);

        List<Certificate> certificatesWithFirstAndSecondTag = certificateDao.findAll(withTags(firstTag.getName(), secondTag.getName()));
        assertEquals(Collections.singletonList(firstCertificate), certificatesWithFirstAndSecondTag);
    }
}