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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TagDaoImplTest {
    private final Certificate firstCertificate = new Certificate(
            "first certificate", "first description", 1, 3);
    private final Certificate secondCertificate = new Certificate(
            "second certificate", "second description", 2, 5);

    private final Tag firstTag = new Tag("first tag");
    private final Tag secondTag = new Tag("second tag");
    private final Tag thirdTag = new Tag("third tag");

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserDao userDao;

    @BeforeAll
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

    @AfterAll
    void destroy() {
        tagDao.delete(firstTag.getId());
        tagDao.delete(secondTag.getId());
        tagDao.delete(thirdTag.getId());
        certificateDao.delete(firstCertificate.getId());
        certificateDao.delete(secondCertificate.getId());
    }

    @Test
    void create() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.create(firstTag));
        assertThrows(DataIntegrityViolationException.class, () -> tagDao.create(new Tag(firstTag.getName())));
        Optional<Tag> persisted = tagDao.get(firstTag.getId());
        assertTrue(persisted.isPresent());
        assertEquals(Arrays.asList(firstCertificate, secondCertificate),
                certificateDao.findAllByTagId(1, 2, persisted.get().getId())
        );
        assertEquals(persisted, tagDao.get(firstTag.getName()));
        assertFalse(tagDao.get(firstTag.getId() + 1000).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.get((Integer) null));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.get((String) null));
    }

    @Test
    void update() {
        assertThrows(UnsupportedOperationException.class, () -> tagDao.update(firstTag));
    }

    @Test
    void createAndDelete() {
        Tag tag = new Tag("tag", new HashSet<>());
        assertDoesNotThrow(() -> tagDao.create(tag));
        Optional<Tag> persisted = tagDao.get(tag.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> tagDao.delete(tag.getId()));
        assertFalse(tagDao.get(tag.getId()).isPresent());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> tagDao.delete(tag.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.delete(null));
    }

    @Test
    @Transactional
    void getTheMostUsedTagOfUserWithTheMaximumCost() {
        Certificate thirdCertificate = new Certificate(
                "third certificate", "third description", 1, 3);
        Certificate forthCertificate = new Certificate(
                "forth certificate", "forth description", 1, 3);
        thirdCertificate.addTag(firstTag);
        thirdCertificate.addTag(thirdTag);
        forthCertificate.addTag(thirdTag);

        certificateDao.create(thirdCertificate);
        certificateDao.create(forthCertificate);

        User firstUser = new User("first", "user", "login1", "password");
        User secondUser = new User("second", "user", "login2", "password");

        userDao.create(firstUser);
        userDao.create(secondUser);

        userOrderDao.create(new UserOrder(firstUser, firstCertificate, 10));
        userOrderDao.create(new UserOrder(firstUser, secondCertificate, 10));
        userOrderDao.create(new UserOrder(firstUser, thirdCertificate, 10));

        userOrderDao.create(new UserOrder(secondUser, secondCertificate, 5));
        userOrderDao.create(new UserOrder(secondUser, thirdCertificate, 5));
        userOrderDao.create(new UserOrder(secondUser, forthCertificate, 5));
        Map<User, List<Tag>> map = tagDao.getTheMostUsedTagsOfUsersWithTheHighestCost();
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertTrue(map.containsKey(firstUser));
        assertEquals(Collections.singletonList(firstTag), map.get(firstUser));
    }

    @Test
    void read() {
        int count = (int) tagDao.getCount();
        assertEquals(3, tagDao.getAll(1, 3).size());
        assertEquals(count, tagDao.getAll(1, count + 1).size());
        assertEquals(2, tagDao.getAll(1, 2).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.getAll(1, -10));

        assertEquals(Arrays.asList(firstTag, secondTag), tagDao.findAllByCertificateId(1, count, firstCertificate.getId()));
        assertEquals(Arrays.asList(firstTag, thirdTag), tagDao.findAllByCertificateId(1, count, secondCertificate.getId()));
    }
}