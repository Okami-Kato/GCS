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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        when(userDao.save(
                new User(firstUser.getFirstName(), firstUser.getLastName(), firstUser.getLogin(), firstUser.getPassword())))
                .thenReturn(firstUser);

        UserResponse actualResponse = userService.create(
                new CreateUserRequest(firstUser.getFirstName(), firstUser.getLastName(), firstUser.getLogin(), firstUser.getPassword()));
        UserResponse expectedResponse = new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName());
        assertEquals(expectedResponse, actualResponse);

        when(userDao.existsByLogin(firstUser.getLogin())).thenReturn(true);
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

        when(userDao.findById(realId)).thenReturn(Optional.of(firstUser));
        when(userDao.findById(notRealId)).thenReturn(Optional.empty());
        when(userDao.findByLogin(realLogin)).thenReturn(Optional.of(firstUser));
        when(userDao.findByLogin(notRealLogin)).thenReturn(Optional.empty());

        Optional<UserResponse> actualResponse = userService.findById(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());
        assertFalse(userService.findById(notRealId).isPresent());

        actualResponse = userService.findByLogin(realLogin);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(userService.findByLogin(notRealLogin).isPresent());
    }

    @Test
    void read() {
        PageRequest firstPageRequest = PageRequest.of(0, 2);
        PageRequest secondPageRequest = PageRequest.of(1, 2);
        PageRequest thirdPageRequest = PageRequest.of(0, 3);

        when(userDao.findAll(firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstUser, secondUser), firstPageRequest, 3));
        when(userDao.findAll(secondPageRequest)).thenReturn(
                new PageImpl<>(Collections.singletonList(thirdUser), secondPageRequest, 3));
        when(userDao.findAll(thirdPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstUser, secondUser, thirdUser), thirdPageRequest, 3)
        );

        UserResponse firstResponse = new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName());
        UserResponse secondResponse = new UserResponse(secondUser.getId(), secondUser.getFirstName(), secondUser.getLastName());
        UserResponse thirdResponse = new UserResponse(thirdUser.getId(), thirdUser.getFirstName(), thirdUser.getLastName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), userService.findAll(firstPageRequest).getContent());
        assertEquals(Collections.singletonList(thirdResponse), userService.findAll(secondPageRequest).getContent());
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), userService.findAll(thirdPageRequest).getContent());

        assertThrows(IllegalArgumentException.class, () -> userService.findAll(PageRequest.of(-1, 2)));
        assertThrows(IllegalArgumentException.class, () -> userService.findAll(PageRequest.of(1, -1)));
    }
}