package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.EntityExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    @Autowired
    private ModelMapper mapper;

    private UserDao userDao;
    private UserService userService;

    private User firstUser = new User("first", "test", "first", "test");
    private User secondUser = new User("second", "test", "second", "test");
    private User thirdUser = new User("third", "test", "third", "test");

    @BeforeAll
    void init() {
        userDao = mock(UserDao.class);
        userService = new UserServiceImpl(userDao, mapper);
        firstUser.setId(1);
        secondUser.setId(2);
        thirdUser.setId(3);
    }

    @Test
    void create() {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((User) args[0]).setId(firstUser.getId());
            return null;
        }).when(userDao).create(new User(firstUser.getFirstName(), firstUser.getLastName(), firstUser.getLogin(), firstUser.getPassword()));

        UserResponse actualResponse = userService.create(
                new CreateUserRequest(firstUser.getFirstName(), firstUser.getLastName(), firstUser.getLogin(), firstUser.getPassword()));
        UserResponse expectedResponse = new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName());
        assertEquals(expectedResponse, actualResponse);

        when(userDao.get(firstUser.getLogin())).thenReturn(Optional.of(firstUser));
        assertThrows(EntityExistsException.class, () -> userService.create(
                new CreateUserRequest(secondUser.getFirstName(), thirdUser.getLastName(), firstUser.getLogin(), firstUser.getPassword())));
        assertThrows(IllegalArgumentException.class, () -> userService.create(null));
    }

    @Test
    void get() {
        int realId = firstUser.getId();
        int notRealId = 2;

        String realLogin = firstUser.getLogin();
        String notRealLogin = "notRealLogin";

        UserResponse expectedResponse = new UserResponse(realId, firstUser.getFirstName(), firstUser.getLastName());

        when(userDao.get(realId)).thenReturn(Optional.of(firstUser));
        when(userDao.get(notRealId)).thenReturn(Optional.empty());
        when(userDao.get(realLogin)).thenReturn(Optional.of(firstUser));
        when(userDao.get(notRealLogin)).thenReturn(Optional.empty());

        Optional<UserResponse> actualResponse = userService.get(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());
        assertFalse(userService.get(notRealId).isPresent());

        actualResponse = userService.get(realLogin);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(userService.get(notRealLogin).isPresent());
    }

    @Test
    void read() {
        when(userDao.getAll(1, 2)).thenReturn(Arrays.asList(firstUser, secondUser));
        when(userDao.getAll(2, 2)).thenReturn(Collections.singletonList(thirdUser));
        when(userDao.getAll(1, 3)).thenReturn(Arrays.asList(firstUser, secondUser, thirdUser));
        when(userDao.getAll(intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(userDao.getAll(anyInt(), intThat(i -> i < 0))).thenThrow(InvalidDataAccessApiUsageException.class);

        UserResponse firstResponse = new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName());
        UserResponse secondResponse = new UserResponse(secondUser.getId(), secondUser.getFirstName(), secondUser.getLastName());
        UserResponse thirdResponse = new UserResponse(thirdUser.getId(), thirdUser.getFirstName(), thirdUser.getLastName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), userService.getAll(1, 2));
        assertEquals(Collections.singletonList(thirdResponse), userService.getAll(2, 2));
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), userService.getAll(1, 3));

        assertThrows(IllegalArgumentException.class, () -> userService.getAll(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> userService.getAll(1, -1));
    }
}