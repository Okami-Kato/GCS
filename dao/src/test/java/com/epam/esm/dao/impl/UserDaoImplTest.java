package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class UserDaoImplTest {
    private final User user = new User("Shannon", "Ramsay", "login", "password");

    @Autowired
    UserDao userDao;

    @Test
    @Sql({"classpath:sql/user-test-data.sql"})
    @Transactional
    void getAll() {
        int count = (int) userDao.getCount();
        assertDoesNotThrow(() -> userDao.getAll(1, count));
        assertEquals(count, userDao.getAll(1, count + 1).size());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.getAll(-1, 10));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.getAll(1, -10));

        User user = userDao.getAll(1, 1).get(0);
        assertDoesNotThrow(() -> userDao.get(user.getId()));
        assertTrue(userDao.get(user.getId()).isPresent());
        assertFalse(userDao.get(user.getId() * (-1)).isPresent());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userDao.get(null));
    }

    @Test
    void create() {
        assertThrows(UnsupportedOperationException.class, () -> userDao.create(user));
    }

    @Test
    void update() {
        assertThrows(UnsupportedOperationException.class, () -> userDao.update(user));
    }

    @Test
    void delete() {
        assertThrows(UnsupportedOperationException.class, () -> userDao.delete(user.getId()));
    }
}