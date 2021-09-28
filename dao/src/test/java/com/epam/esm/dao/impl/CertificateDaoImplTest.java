package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.util.Sort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
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
        tagDao.create(firstTag);
        tagDao.create(secondTag);

        firstCertificate.addTag(firstTag);
        firstCertificate.addTag(secondTag);
        secondCertificate.addTag(firstTag);
        thirdCertificate.addTag(secondTag);
    }

    @AfterAll
    void destroy(){
        tagDao.delete(firstTag.getId());
        tagDao.delete(secondTag.getId());
    }

    @BeforeEach
    void createAll() {
        certificateDao.create(firstCertificate);
        certificateDao.create(secondCertificate);
        certificateDao.create(thirdCertificate);
    }

    @AfterEach
    void deleteAll() {
        certificateDao.delete(firstCertificate.getId());
        certificateDao.delete(secondCertificate.getId());
        certificateDao.delete(thirdCertificate.getId());
        firstCertificate.setId(null);
        secondCertificate.setId(null);
        thirdCertificate.setId(null);
    }

    @Test
    void create() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.create(firstCertificate));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.create(null));
        Optional<Certificate> persisted = certificateDao.get(firstCertificate.getId());
        assertTrue(persisted.isPresent());
        assertTrue(persisted.get().getTags().containsAll(Arrays.asList(firstTag, secondTag)));
        assertFalse(certificateDao.get(firstCertificate.getId() + 1000).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.get(null));
    }

    @Test
    void update() {
        Certificate certificate = certificateDao.get(thirdCertificate.getId()).get();
        certificate.setName("new name");
        certificate.setDescription("new description");
        certificate.addTag(firstTag);
        assertDoesNotThrow(() -> certificateDao.update(certificate));
        Optional<Certificate> persisted = certificateDao.get(thirdCertificate.getId());
        assertTrue(persisted.isPresent());
        assertNotNull(persisted.get().getLastUpdateDate());
        assertEquals(certificate.getName(), persisted.get().getName());
        assertEquals(certificate.getDescription(), persisted.get().getDescription());
        assertEquals(certificate.getTags(), persisted.get().getTags());

        certificate.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> certificateDao.update(certificate));
        certificate.setName("name");
        certificate.setId(thirdCertificate.getId() * (-1));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.update(certificate));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.update(null));
    }

    @Test
    void createAndDelete() {
        Certificate certificate = new Certificate("certificate", "description", 1, 3);
        assertDoesNotThrow(() -> certificateDao.create(certificate));
        Optional<Certificate> persisted = certificateDao.get(certificate.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> certificateDao.delete(certificate.getId()));
        assertFalse(certificateDao.get(certificate.getId()).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.delete(certificate.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.delete(null));
    }

    @Test
    void getAll() {
        assertEquals(3, certificateDao.getAll(1, 3).size());
        assertEquals(2, certificateDao.getAll(1, 2).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(1, -10));

        int count = (int) certificateDao.getCount();
        assertEquals(3, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInName("certificate").build()).size());
        assertEquals(1, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInName("first").build()).size());
        assertEquals(0, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInName("certificatee").build()).size());

        assertEquals(3, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInDescription("description").build()).size());
        assertEquals(1, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInDescription("first").build()).size());
        assertEquals(0, certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withPartInDescription("descriptionn").build()).size());

        List<Certificate> all = certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withTags(firstTag.getId()).build());
        assertEquals(2, all.size());
        assertTrue(all.containsAll(Arrays.asList(firstCertificate, secondCertificate)));

        all = certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withTags(secondTag.getId()).build());
        assertEquals(2, all.size());
        assertTrue(all.containsAll(Arrays.asList(firstCertificate, thirdCertificate)));

        all = certificateDao.getAll(1, count,
                CertificateFilter.newBuilder().withTags(firstTag.getId(), secondTag.getId()).build());
        assertEquals(1, all.size());
        assertTrue(all.contains(firstCertificate));

        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(1, count,
                CertificateFilter.newBuilder()
                        .withSort(Sort.by(Sort.Order.desc(null)))
                        .build())
        );
        assertThrows(InvalidDataAccessApiUsageException.class, () -> certificateDao.getAll(1, count,
                CertificateFilter.newBuilder()
                        .withSort(Sort.by(Sort.Order.desc("")))
                        .build())
        );
    }
}