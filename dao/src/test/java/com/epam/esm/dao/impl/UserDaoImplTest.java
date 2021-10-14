package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
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
    UserDao userDao;

    @BeforeAll
    void init() {
        userDao.create(firstUser);
        userDao.create(secondUser);
        userDao.create(thirdUser);
    }

    @AfterAll
    void destroy() {
        userDao.delete(firstUser.getId());
        userDao.delete(secondUser.getId());
        userDao.delete(thirdUser.getId());
    }

    @Test
    void createAndDelete() {
        User user = new User("new", "user", "new login", "password");
        assertDoesNotThrow(() -> userDao.create(user));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.create(firstUser));
        assertThrows(DataIntegrityViolationException.class, () -> userDao.create(
                new User("first name", "last name", user.getLogin(), "password")
        ));
        Optional<User> persisted = userDao.get(user.getId());
        assertTrue(persisted.isPresent());
        assertDoesNotThrow(() -> userDao.delete(user.getId()));
        assertFalse(userDao.get(user.getId()).isPresent());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> userDao.delete(user.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.delete(null));
    }

    @Test
    void read() {
        int count = (int) userDao.getCount();
        assertDoesNotThrow(() -> userDao.getAll(1, count));
        assertEquals(count, userDao.getAll(1, count + 1).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.getAll(1, -10));

        assertDoesNotThrow(() -> userDao.get(firstUser.getId()));
        assertTrue(userDao.get(firstUser.getId()).isPresent());
        assertFalse(userDao.get(firstUser.getId() * (-1)).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.get((Integer) null));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.get((String) null));
    }

    @Test
    void update() {
        assertThrows(UnsupportedOperationException.class, () -> userDao.update(firstUser));
    }
}