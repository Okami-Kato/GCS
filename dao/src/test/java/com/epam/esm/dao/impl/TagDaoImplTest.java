package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.dao.config.DaoConfig;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = DaoConfig.class)
@ActiveProfiles("test")
class TagDaoImplTest {
    private final Certificate firstCertificate = new Certificate(
            "first certificate", "first description", 1, 3, Instant.now(), Instant.now(), new HashSet<>()
    );
    private final Certificate secondCertificate = new Certificate(
            "second certificate", "second description", 2, 5, Instant.now(), Instant.now(), new HashSet<>()
    );

    private final Tag firstTag = new Tag("first tag", new HashSet<>());
    private final Tag secondTag = new Tag("second tag", new HashSet<>());
    private final Tag thirdTag = new Tag("third tag", new HashSet<>());

    @Autowired
    private CertificateDao certificateDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserDao userDao;

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
    void create() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.create(firstTag));
        Optional<Tag> persisted = tagDao.get(firstTag.getId());
        assertTrue(persisted.isPresent());
        assertTrue(persisted.get().getCertificates().containsAll(Arrays.asList(firstCertificate, secondCertificate)));
        assertFalse(tagDao.get(firstTag.getId() + 1000).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.get(null));
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
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.delete(tag.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.delete(null));
    }

    @Test
    @Transactional
    @Sql("classpath:sql/user-test-data.sql")
    void getTheMostUsedTagOfUserWithTheMaximumCost() {
        Certificate thirdCertificate = new Certificate(
                "third certificate", "third description", 1, 3, Instant.now(), Instant.now(), new HashSet<>()
        );
        Certificate forthCertificate = new Certificate(
                "forth certificate", "forth description", 1, 3, Instant.now(), Instant.now(), new HashSet<>()
        );
        thirdCertificate.addTag(firstTag);
        thirdCertificate.addTag(thirdTag);
        forthCertificate.addTag(thirdTag);

        certificateDao.create(thirdCertificate);
        certificateDao.create(forthCertificate);

        Optional<User> optionalUser = userDao.get(1);
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        userOrderDao.create(new UserOrder(user, firstCertificate, 10 ,Instant.now()));
        userOrderDao.create(new UserOrder(user, secondCertificate, 10 ,Instant.now()));
        userOrderDao.create(new UserOrder(user, thirdCertificate, 10 ,Instant.now()));

        optionalUser = userDao.get(2);
        assertTrue(optionalUser.isPresent());
        user = optionalUser.get();
        userOrderDao.create(new UserOrder(user, secondCertificate, 5 ,Instant.now()));
        userOrderDao.create(new UserOrder(user, thirdCertificate, 5 ,Instant.now()));
        userOrderDao.create(new UserOrder(user, forthCertificate, 5 ,Instant.now()));

        assertEquals(firstTag, tagDao.getTheMostUsedTagOfUserWithTheMaximumCost());
    }

    @Test
    void getAll() {
        assertEquals(3, tagDao.getAll(1, 3).size());
        assertEquals(2, tagDao.getAll(1, 2).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tagDao.getAll(1, -10));

        int count = (int) tagDao.getCount();
        assertEquals(Arrays.asList(firstTag, secondTag), tagDao.getAll(1, count, firstCertificate.getId()));
        assertEquals(Arrays.asList(firstTag, thirdTag), tagDao.getAll(1, count, secondCertificate.getId()));
    }
}