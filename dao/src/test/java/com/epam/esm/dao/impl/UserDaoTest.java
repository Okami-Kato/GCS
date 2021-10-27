package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
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
class UserDaoTest {
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
        User user = User.builder()
                .fullName("new user")
                .username("new")
                .password("password")
                .build();
        assertDoesNotThrow(() -> userDao.save(user));
        assertThrows(DataIntegrityViolationException.class, () -> userDao.save(
                User.builder()
                        .fullName("name")
                        .username(user.getUsername())
                        .password("password")
                        .build()
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
        Certificate firstCertificate = Certificate.builder()
                .name("first certificate")
                .description("first description")
                .price(10)
                .duration(3)
                .build();
        Certificate secondCertificate = Certificate.builder()
                .name("second certificate")
                .description("second description")
                .price(10)
                .duration(3)
                .build();
        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate));
        userOrderDao.saveAll(Arrays.asList(
                UserOrder.builder()
                        .user(firstUser)
                        .certificate(firstCertificate)
                        .cost(firstCertificate.getPrice())
                        .build(),
                UserOrder.builder()
                        .user(firstUser)
                        .certificate(secondCertificate)
                        .cost(secondCertificate.getPrice())
                        .build(),
                UserOrder.builder()
                        .user(secondUser)
                        .certificate(firstCertificate)
                        .cost(firstCertificate.getPrice())
                        .build()
        ));
        assertEquals(Collections.singletonList(firstUser), userDao.findUsersWithTheHighestCost());
        userOrderDao.save(UserOrder.builder()
                .user(secondUser)
                .certificate(secondCertificate)
                .cost(secondCertificate.getPrice())
                .build());
        assertEquals(Arrays.asList(firstUser, secondUser), userDao.findUsersWithTheHighestCost());

    }
}