package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.config.DaoConfig;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = DaoConfig.class)
@ActiveProfiles("test")
class CertificateDaoImplTest {
    private Certificate certificate;
    private Tag firstTag = new Tag("first tag", new HashSet<>());
    private Tag secondTag = new Tag("second tag", new HashSet<>());

    @Autowired
    private CertificateDao certificateDao;

    @BeforeEach
    void afterEach() {
        certificate = new Certificate(
                "name", "description", 5, 5, Instant.now(), Instant.now(), new HashSet<>()
        );
        certificate.addTag(firstTag);
        certificate.addTag(secondTag);
    }

    @Test
    void create() {
        assertDoesNotThrow(() -> certificateDao.create(certificate));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.create(certificate));
        assertTrue(certificateDao.get(certificate.getId()).isPresent());
        assertFalse(certificateDao.get(certificate.getId() + 1).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.get(null));
    }

    @Test
    void update() {
        certificateDao.create(certificate);
        certificate.setName("new name");
        certificate.setDescription("new description");
        assertDoesNotThrow(() -> certificateDao.update(certificate));
        Optional<Certificate> persisted = certificateDao.get(certificate.getId());
        assertTrue(persisted.isPresent());
        assertEquals(persisted.get(), certificate);

        certificate.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> certificateDao.update(certificate));
    }

    @Test
    void delete() {
        certificateDao.create(certificate);
        Optional<Certificate> persisted = certificateDao.get(certificate.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> certificateDao.delete(certificate.getId()));
        assertFalse(certificateDao.get(certificate.getId()).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.delete(certificate.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.delete(null));
    }

    @Test
    void getAll(){

    }
}