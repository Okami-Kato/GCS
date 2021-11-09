package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.UserOrder;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TagDaoTest {
    private final Tag firstTag = Tag.builder().name("first tag").build();
    private final Tag secondTag = Tag.builder().name("second tag").build();
    private final Tag thirdTag = Tag.builder().name("third tag").build();

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
            .tags(Sets.newHashSet(firstTag, thirdTag))
            .build();

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private UserOrderDao userOrderDao;

    @BeforeAll
    void init() {
        tagDao.saveAll(Arrays.asList(firstTag, secondTag, thirdTag));
        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate));
    }

    @AfterAll
    void destroy() {
        tagDao.deleteAllByIdInBatch(Arrays.asList(firstTag.getId(), secondTag.getId(), thirdTag.getId()));
        certificateDao.deleteAllByIdInBatch(Arrays.asList(firstCertificate.getId(), secondCertificate.getId()));
    }

    @Test
    void create() {
        assertThrows(DataIntegrityViolationException.class, () -> tagDao.save(Tag.builder().name(firstTag.getName()).build()));
        Optional<Tag> persisted = tagDao.findById(firstTag.getId());
        assertTrue(persisted.isPresent());
        assertEquals(Arrays.asList(firstCertificate, secondCertificate),
                certificateDao.findAllByTagsId(persisted.get().getId(), Pageable.unpaged()).stream().collect(Collectors.toList())
        );
        assertEquals(persisted, tagDao.findByName(firstTag.getName()));
        assertFalse(tagDao.findById(firstTag.getId() + 1000).isPresent());
    }

    @Test
    void createAndDelete() {
        Tag tag = Tag.builder().name("tag").build();
        assertDoesNotThrow(() -> tagDao.save(tag));
        Optional<Tag> persisted = tagDao.findById(tag.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> tagDao.deleteById(tag.getId()));
        assertFalse(tagDao.findById(tag.getId()).isPresent());
        assertThrows(EmptyResultDataAccessException.class, () -> tagDao.deleteById(tag.getId()));
    }

    @Test
    @Transactional
    void getTheMostUsedTagOfUser() {
        Certificate thirdCertificate = Certificate.builder()
                .name("third certificate")
                .description("third description")
                .price(2)
                .duration(7)
                .tags(Sets.newHashSet(firstTag, thirdTag))
                .build();
        Certificate forthCertificate = Certificate.builder()
                .name("forth certificate")
                .description("forth description")
                .price(2)
                .duration(7)
                .tags(Sets.newHashSet(thirdTag))
                .build();

        certificateDao.save(thirdCertificate);
        certificateDao.save(forthCertificate);

        String firstUserId = "firstUser";
        String secondUserId = "secondUser";

        userOrderDao.save(UserOrder.builder()
                .userId(firstUserId)
                .certificate(firstCertificate)
                .cost(firstCertificate.getPrice())
                .build());
        userOrderDao.save(UserOrder.builder()
                .userId(firstUserId)
                .certificate(thirdCertificate)
                .cost(thirdCertificate.getPrice())
                .build());

        userOrderDao.save(UserOrder.builder()
                .userId(secondUserId)
                .certificate(secondCertificate)
                .cost(secondCertificate.getPrice())
                .build());
        userOrderDao.save(UserOrder.builder()
                .userId(secondUserId)
                .certificate(thirdCertificate)
                .cost(thirdCertificate.getPrice())
                .build());

        List<Tag> theMostUsedTagsOfFirstUser = tagDao.findTheMostUsedTagsOfUser(firstUserId);
        assertEquals(Collections.singletonList(firstTag), theMostUsedTagsOfFirstUser);

        List<Tag> theMostUsedTagsOfSecondUser = tagDao.findTheMostUsedTagsOfUser(secondUserId);
        assertEquals(Arrays.asList(firstTag, thirdTag), theMostUsedTagsOfSecondUser);
    }
}