package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserDaoImplTest {
    private final User firstUser = new User("first", "user", "login1", "password");
    private final User secondUser = new User("second", "user", "login2", "password");
    private final User thirdUser = new User("third", "user", "login3", "password");

    @Autowired
    CertificateDao certificateDao;
    @Autowired
    UserDao userDao;
    @Autowired
    UserOrderDao userOrderDao;

    @BeforeAll
    void init() {
        userDao.saveAll(Arrays.asList(firstUser, secondUser, thirdUser));
    }

    @AfterAll
    void destroy() {
        userDao.deleteAllByIdInBatch(Arrays.asList(firstUser.getId(), secondUser.getId(), thirdUser.getId()));
    }

    @Test
    void createAndDelete() {
        User user = new User("new", "user", "new login", "password");
        assertDoesNotThrow(() -> userDao.save(user));
        assertThrows(DataIntegrityViolationException.class, () -> userDao.save(
                new User("first name", "last name", user.getLogin(), "password")
        ));
        Optional<User> persisted = userDao.findById(user.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> userDao.deleteById(user.getId()));
        assertFalse(userDao.findById(user.getId()).isPresent());
        assertThrows(EmptyResultDataAccessException.class, () -> userDao.deleteById(user.getId()));
    }

    @Test
    @Transactional
    void findUsersWithTheHighestCost() {
        Certificate firstCertificate = new Certificate(
                "first certificate", "first description", 10, 3);
        Certificate secondCertificate = new Certificate(
                "second certificate", "second description", 10, 5);
        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate));
        userOrderDao.saveAll(Arrays.asList(
                new UserOrder(firstUser, firstCertificate, 10),
                new UserOrder(firstUser, secondCertificate, 10),
                new UserOrder(secondUser, firstCertificate, 10)
        ));
        assertEquals(Collections.singletonList(firstUser), userDao.findUsersWithTheHighestCost());
        userOrderDao.save(new UserOrder(secondUser, secondCertificate, 10));
        assertEquals(Arrays.asList(firstUser, secondUser), userDao.findUsersWithTheHighestCost());

    }
}