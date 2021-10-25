package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserOrderDaoTest {
    private final Certificate firstCertificate = new Certificate(
            "first certificate", "first description", 1, 3);
    private final Certificate secondCertificate = new Certificate(
            "second certificate", "second description", 2, 5);
    private final Certificate thirdCertificate = new Certificate(
            "third certificate", "third description", 4, 12);

    private final Tag firstTag = new Tag("first tag", new HashSet<>());
    private final Tag secondTag = new Tag("second tag", new HashSet<>());
    private final Tag thirdTag = new Tag("third tag", new HashSet<>());

    private final User firstUser = new User("first", "user", "login1", "password");
    private final User secondUser = new User("second", "user", "login2", "password");
    private final User thirdUser = new User("third", "user", "login3", "password");

    UserOrder firstOrder = new UserOrder(firstUser, firstCertificate, firstCertificate.getPrice());
    UserOrder secondOrder = new UserOrder(firstUser, secondCertificate, secondCertificate.getPrice());
    UserOrder thirdOrder = new UserOrder(secondUser, firstCertificate, firstCertificate.getPrice());
    UserOrder forthOrder = new UserOrder(secondUser, secondCertificate, secondCertificate.getPrice());

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

        firstTag.addCertificate(firstCertificate);
        firstTag.addCertificate(secondCertificate);
        secondTag.addCertificate(firstCertificate);
        thirdTag.addCertificate(secondCertificate);

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
        UserOrder order = new UserOrder(thirdUser, thirdCertificate, thirdCertificate.getPrice());
        assertDoesNotThrow(() -> userOrderDao.save(order));
        Optional<UserOrder> persisted = userOrderDao.findById(order.getId());
        assertTrue(persisted.isPresent());
        assertFalse(userOrderDao.findById(order.getId() * (-1)).isPresent());
        assertEquals(thirdUser, persisted.get().getUser());
        assertEquals(thirdCertificate, persisted.get().getCertificate());
    }
}