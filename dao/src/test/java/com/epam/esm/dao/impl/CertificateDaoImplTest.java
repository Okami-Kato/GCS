package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.config.DaoConfig;
import com.epam.esm.entity.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private static Certificate CERTIFICATE;

    @Autowired
    private CertificateDao certificateDao;

    @BeforeEach
    void afterEach() {
        CERTIFICATE = new Certificate(
                "name", "description", 5, 5, Instant.now(), Instant.now(), new HashSet<>()
        );
    }

    @Test
    void create() {
        assertDoesNotThrow(() -> certificateDao.create(CERTIFICATE));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.create(CERTIFICATE));
        assertTrue(certificateDao.get(CERTIFICATE.getId()).isPresent());
        assertFalse(certificateDao.get(CERTIFICATE.getId() + 1).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.get(null));
    }

    @Test
    void update() {
        certificateDao.create(CERTIFICATE);
        CERTIFICATE.setName("new name");
        CERTIFICATE.setDescription("new description");
        assertDoesNotThrow(() -> certificateDao.update(CERTIFICATE));
        Optional<Certificate> persisted = certificateDao.get(CERTIFICATE.getId());
        assertTrue(persisted.isPresent());
        assertEquals(persisted.get(), CERTIFICATE);
    }

    @Test
    void delete() {
        certificateDao.create(CERTIFICATE);
        Optional<Certificate> persisted = certificateDao.get(CERTIFICATE.getId());
        assertTrue(persisted.isPresent());
        certificateDao.delete(CERTIFICATE.getId());
        assertFalse(certificateDao.get(CERTIFICATE.getId()).isPresent());
    }

    @Test
    void getAll(){

    }
}