package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
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

    private final User firstUser = User.builder()
            .fullName("first user")
            .username("first")
            .password("password")
            .build();
    private final User secondUser = User.builder()
            .fullName("second user")
            .username("second")
            .password("password")
            .build();
    private final User thirdUser = User.builder()
            .fullName("third user")
            .username("third")
            .password("password")
            .build();

    private final UserOrder firstOrder = UserOrder.builder()
            .user(firstUser)
            .certificate(firstCertificate)
            .cost(firstCertificate.getPrice())
            .build();
    private final UserOrder secondOrder = UserOrder.builder()
            .user(firstUser)
            .certificate(secondCertificate)
            .cost(secondCertificate.getPrice())
            .build();
    private final UserOrder thirdOrder = UserOrder.builder()
            .user(secondUser)
            .certificate(firstCertificate)
            .cost(firstCertificate.getPrice())
            .build();
    private final UserOrder forthOrder = UserOrder.builder()
            .user(secondUser)
            .certificate(secondCertificate)
            .cost(secondCertificate.getPrice())
            .build();

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserOrderDao userOrderDao;

    @BeforeAll
    void init() {
        userDao.saveAll(Arrays.asList(firstUser, secondUser, thirdUser));
        tagDao.saveAll(Arrays.asList(firstTag, secondTag, thirdTag));

        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate));
        userOrderDao.saveAll(Arrays.asList(firstOrder, secondOrder, thirdOrder, forthOrder));
    }

    @AfterAll
    void destroy() {
        userOrderDao.deleteAllByIdInBatch(Arrays.asList(firstOrder.getId(), secondOrder.getId(), thirdOrder.getId(), forthOrder.getId()));
        userDao.deleteAllByIdInBatch(Arrays.asList(firstUser.getId(), secondUser.getId(), thirdUser.getId()));
        tagDao.deleteAllByIdInBatch(Arrays.asList(firstTag.getId(), secondTag.getId(), thirdTag.getId()));
        certificateDao.deleteAllByIdInBatch(Arrays.asList(firstCertificate.getId(), secondCertificate.getId(), thirdCertificate.getId()));
    }

    @Test
    @Transactional
    void create() {
        UserOrder order = UserOrder.builder()
                .user(thirdUser)
                .certificate(thirdCertificate)
                .cost(thirdCertificate.getPrice())
                .build();
        assertDoesNotThrow(() -> userOrderDao.save(order));
        Optional<UserOrder> persisted = userOrderDao.findById(order.getId());
        assertTrue(persisted.isPresent());
        assertFalse(userOrderDao.findById(order.getId() * (-1)).isPresent());
        assertEquals(thirdUser, persisted.get().getUser());
        assertEquals(thirdCertificate, persisted.get().getCertificate());
    }
}