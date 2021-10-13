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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserOrderDaoImplTest {
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
        userDao.create(firstUser);
        userDao.create(secondUser);
        userDao.create(thirdUser);

        tagDao.create(firstTag);
        tagDao.create(secondTag);
        tagDao.create(thirdTag);

        firstTag.addCertificate(firstCertificate);
        firstTag.addCertificate(secondCertificate);
        secondTag.addCertificate(firstCertificate);
        thirdTag.addCertificate(secondCertificate);

        certificateDao.create(firstCertificate);
        certificateDao.create(secondCertificate);
        certificateDao.create(thirdCertificate);

        userOrderDao.create(firstOrder);
        userOrderDao.create(secondOrder);
        userOrderDao.create(thirdOrder);
        userOrderDao.create(forthOrder);
    }

    @AfterAll
    void destroy(){
        userDao.delete(firstUser.getId());
        userDao.delete(secondUser.getId());
        userDao.delete(thirdUser.getId());

        tagDao.delete(firstTag.getId());
        tagDao.delete(secondTag.getId());
        tagDao.delete(thirdTag.getId());

        certificateDao.delete(firstCertificate.getId());
        certificateDao.delete(secondCertificate.getId());
        certificateDao.delete(thirdCertificate.getId());

        userOrderDao.delete(firstOrder.getId());
        userOrderDao.delete(secondOrder.getId());
        userOrderDao.delete(thirdOrder.getId());
        userOrderDao.delete(forthOrder.getId());
    }

    @Test
    void create() {
        UserOrder order = new UserOrder(thirdUser, thirdCertificate, thirdCertificate.getPrice());
        assertDoesNotThrow(() -> userOrderDao.create(order));
        Optional<UserOrder> persisted = userOrderDao.get(order.getId());
        assertTrue(persisted.isPresent());
        assertFalse(userOrderDao.get(order.getId() * (-1)).isPresent());
        assertEquals(thirdUser, persisted.get().getUser());
        assertEquals(thirdCertificate, persisted.get().getCertificate());
    }

    @Test
    void read(){
        int count = (int) userOrderDao.getCount();
        assertDoesNotThrow(() -> userOrderDao.getAll(1, count));
        assertEquals(count, userOrderDao.getAll(1, count + 1).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userOrderDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userOrderDao.getAll(1, -10));

        assertDoesNotThrow(() -> userOrderDao.get(firstOrder.getId()));
        assertTrue(userOrderDao.get(firstOrder.getId()).isPresent());
        assertFalse(userOrderDao.get(firstOrder.getId() * (-1)).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userOrderDao.get(null));

        assertEquals(Arrays.asList(firstOrder, secondOrder), userOrderDao.findAllByUserId(1, count, firstUser.getId()));
        assertEquals(Arrays.asList(thirdOrder, forthOrder), userOrderDao.findAllByUserId(1, count, secondUser.getId()));

        assertEquals(Arrays.asList(firstOrder, thirdOrder), userOrderDao.findAllByCertificateId(1, count, firstCertificate.getId()));
        assertEquals(Arrays.asList(secondOrder, forthOrder), userOrderDao.findAllByCertificateId(1, count, secondCertificate.getId()));
    }

    @Test
    void update() {
        assertThrows(UnsupportedOperationException.class, () -> userOrderDao.update(firstOrder));
    }
}