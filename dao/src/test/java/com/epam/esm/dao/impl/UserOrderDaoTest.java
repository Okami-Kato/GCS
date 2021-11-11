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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserOrderDaoTest {
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
    private final Certificate thirdCertificate = Certificate.builder()
            .name("third certificate")
            .description("third description")
            .price(2)
            .duration(7)
            .build();

    private final String firstUserId = "first user";
    private final String secondUserId = "second user";
    private final String thirdUserId = "third user";

    private final UserOrder firstOrder = UserOrder.builder()
            .userId(firstUserId)
            .certificate(firstCertificate)
            .cost(firstCertificate.getPrice())
            .build();
    private final UserOrder secondOrder = UserOrder.builder()
            .userId(firstUserId)
            .certificate(secondCertificate)
            .cost(secondCertificate.getPrice())
            .build();
    private final UserOrder thirdOrder = UserOrder.builder()
            .userId(secondUserId)
            .certificate(firstCertificate)
            .cost(firstCertificate.getPrice())
            .build();
    private final UserOrder forthOrder = UserOrder.builder()
            .userId(secondUserId)
            .certificate(secondCertificate)
            .cost(secondCertificate.getPrice())
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

        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate));
        userOrderDao.saveAll(Arrays.asList(firstOrder, secondOrder, thirdOrder, forthOrder));
    }

    @AfterAll
    void destroy() {
        userOrderDao.deleteAllByIdInBatch(Arrays.asList(firstOrder.getId(), secondOrder.getId(), thirdOrder.getId(), forthOrder.getId()));
        tagDao.deleteAllByIdInBatch(Arrays.asList(firstTag.getId(), secondTag.getId(), thirdTag.getId()));
        certificateDao.deleteAllByIdInBatch(Arrays.asList(firstCertificate.getId(), secondCertificate.getId(), thirdCertificate.getId()));
    }

    @Test
    @Transactional
    void create() {
        UserOrder order = UserOrder.builder()
                .userId(thirdUserId)
                .certificate(thirdCertificate)
                .cost(thirdCertificate.getPrice())
                .build();
        assertDoesNotThrow(() -> userOrderDao.save(order));
        Optional<UserOrder> persisted = userOrderDao.findById(order.getId());
        assertTrue(persisted.isPresent());
        assertFalse(userOrderDao.findById(order.getId() * (-1)).isPresent());
        assertEquals(thirdUserId, persisted.get().getUserId());
        assertEquals(thirdCertificate, persisted.get().getCertificate());
    }

    @Test
    @Transactional
    void userWithTheHighestCost() {
        userOrderDao.saveAll(Arrays.asList(
                UserOrder.builder()
                        .userId(firstUserId)
                        .certificate(firstCertificate)
                        .cost(firstCertificate.getPrice())
                        .build(),
                UserOrder.builder()
                        .userId(firstUserId)
                        .certificate(secondCertificate)
                        .cost(secondCertificate.getPrice())
                        .build(),
                UserOrder.builder()
                        .userId(secondUserId)
                        .certificate(firstCertificate)
                        .cost(firstCertificate.getPrice())
                        .build()
        ));
        assertEquals(Collections.singletonList(firstUserId), userOrderDao.findUsersWithTheHighestCost());
        userOrderDao.save(UserOrder.builder()
                .userId(secondUserId)
                .certificate(secondCertificate)
                .cost(secondCertificate.getPrice())
                .build());
        assertEquals(Arrays.asList(firstUserId, secondUserId), userOrderDao.findUsersWithTheHighestCost());
    }
}