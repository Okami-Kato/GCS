package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class UserOrderDaoImplTest {
    private final Certificate firstCertificate = new Certificate(
            "first certificate", "first description", 1, 3);
    private final Certificate secondCertificate = new Certificate(
            "second certificate", "second description", 2, 5);

    private final Tag firstTag = new Tag("first tag", new HashSet<>());
    private final Tag secondTag = new Tag("second tag", new HashSet<>());
    private final Tag thirdTag = new Tag("third tag", new HashSet<>());

    private User user;
    private UserOrder order;

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserOrderDao userOrderDao;

    @PostConstruct
    void init() {
        tagDao.create(firstTag);
        tagDao.create(secondTag);
        tagDao.create(thirdTag);

        firstTag.addCertificate(firstCertificate);
        firstTag.addCertificate(secondCertificate);
        secondTag.addCertificate(firstCertificate);
        thirdTag.addCertificate(secondCertificate);

        certificateDao.create(firstCertificate);
        certificateDao.create(secondCertificate);
    }

    @Test
    @Sql({"classpath:sql/user-test-data.sql"})
    @Transactional
    void create() {
        user = userDao.getAll(1, 1).get(0);
        order = new UserOrder(user, firstCertificate, firstCertificate.getPrice());
        assertDoesNotThrow(() -> userOrderDao.create(order));
        Optional<UserOrder> persisted = userOrderDao.get(order.getId());
        assertTrue(persisted.isPresent());
        assertFalse(userOrderDao.get(order.getId() * (-1)).isPresent());
        assertEquals(user, persisted.get().getUser());
        assertEquals(firstCertificate, persisted.get().getCertificate());
    }

    @Test
    void update() {
        assertThrows(UnsupportedOperationException.class, () -> userOrderDao.update(order));
    }
}