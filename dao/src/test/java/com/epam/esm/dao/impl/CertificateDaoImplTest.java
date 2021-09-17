package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.config.DaoConfig;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
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
    private final Certificate certificate = new Certificate(
            "name", "description", 5, 5, Instant.now(), Instant.now(), new HashSet<>()
    );

    private final Tag firstTag = new Tag("first tag", new HashSet<>());
    private final Tag secondTag = new Tag("second tag", new HashSet<>());


    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;

    @PostConstruct
    void init() {
        tagDao.create(firstTag);
        tagDao.create(secondTag);
    }

    @AfterEach
    void resetIds(){
        certificate.setId(null);
    }

    @Test
    void create() {
        certificate.addTag(firstTag);
        certificate.addTag(secondTag);

        assertDoesNotThrow(() -> certificateDao.create(certificate));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.create(certificate));
        Optional<Certificate> optionalCertificate = certificateDao.get(certificate.getId());
        assertTrue(optionalCertificate.isPresent());
        assertTrue(optionalCertificate.get().getTags().containsAll(Arrays.asList(firstTag, secondTag)));
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
    void getAll() {
        certificateDao.create(certificate);
        Certificate certificate1 = new Certificate(
                "name", "description", 5, 5, Instant.now(), Instant.now(), new HashSet<>()
        );
        certificateDao.create(certificate1);
        Certificate certificate2 = new Certificate(
                "name", "description", 5, 5, Instant.now(), Instant.now(), new HashSet<>()
        );
        certificateDao.create(certificate2);
        assertEquals(3, certificateDao.getAll(1, 3).size());
        assertEquals(2, certificateDao.getAll(1, 2).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(1, -10));
    }
}