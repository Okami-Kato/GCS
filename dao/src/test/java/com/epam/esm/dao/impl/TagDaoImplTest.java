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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
        tagDao.saveAll(Arrays.asList(firstTag, secondTag, thirdTag));

        firstTag.addCertificate(firstCertificate);
        firstTag.addCertificate(secondCertificate);
        secondTag.addCertificate(firstCertificate);
        thirdTag.addCertificate(secondCertificate);

        certificateDao.saveAll(Arrays.asList(firstCertificate, secondCertificate));
    }

    @AfterAll
    void destroy() {
        tagDao.deleteAllByIdInBatch(Arrays.asList(firstTag.getId(), secondTag.getId(), thirdTag.getId()));
        certificateDao.deleteAllByIdInBatch(Arrays.asList(firstCertificate.getId(), secondCertificate.getId()));
    }

    @Test
    void create() {
        assertThrows(DataIntegrityViolationException.class, () -> tagDao.save(new Tag(firstTag.getName())));
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
        Tag tag = new Tag("tag", new HashSet<>());
        assertDoesNotThrow(() -> tagDao.save(tag));
        Optional<Tag> persisted = tagDao.findById(tag.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> tagDao.deleteById(tag.getId()));
        assertFalse(tagDao.findById(tag.getId()).isPresent());
        assertThrows(EmptyResultDataAccessException.class, () -> tagDao.deleteById(tag.getId()));
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

        certificateDao.save(thirdCertificate);
        certificateDao.save(forthCertificate);

        User firstUser = new User("first", "user", "login1", "password");
        User secondUser = new User("second", "user", "login2", "password");

        userDao.save(firstUser);
        userDao.save(secondUser);

        userOrderDao.save(new UserOrder(firstUser, firstCertificate, 10));
        userOrderDao.save(new UserOrder(firstUser, secondCertificate, 10));
        userOrderDao.save(new UserOrder(firstUser, thirdCertificate, 10));

        userOrderDao.save(new UserOrder(secondUser, secondCertificate, 5));
        userOrderDao.save(new UserOrder(secondUser, thirdCertificate, 5));

        List<Tag> theMostUsedTagsOfFirstUser = tagDao.findTheMostUsedTagsOfUser(firstUser.getId());
        assertEquals(Collections.singletonList(firstTag), theMostUsedTagsOfFirstUser);

        List<Tag> theMostUsedTagsOfSecondUser = tagDao.findTheMostUsedTagsOfUser(secondUser.getId());
        assertEquals(Arrays.asList(firstTag, thirdTag), theMostUsedTagsOfSecondUser);
    }
}