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

    private final User firstUser = User.builder()
            .id(1)
            .fullName("first user")
            .username("first")
            .password("password")
            .build();
    private final User secondUser = User.builder()
            .id(2)
            .fullName("second user")
            .username("second")
            .password("password")
            .build();
    private final User thirdUser = User.builder()
            .id(3)
            .fullName("third user")
            .username("third")
            .password("password")
            .build();

    @BeforeAll
    void init() {
        userDao = mock(UserDao.class);
        userService = new UserServiceImpl(userDao, mapper);
    }

    @Test
    void create() {
        when(userDao.save(
                new User(firstUser.getId(), firstUser.getFullName(),
                        firstUser.getUsername(), firstUser.getPassword(), firstUser.getOrders())))
                .thenReturn(firstUser);

        UserResponse actualResponse = userService.create(
                new CreateUserRequest(firstUser.getFullName(), firstUser.getUsername(), firstUser.getPassword()));
        UserResponse expectedResponse = new UserResponse(firstUser.getId(), firstUser.getFullName());
        assertEquals(expectedResponse, actualResponse);

        when(userDao.existsByUsername(firstUser.getUsername())).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> userService.create(
                new CreateUserRequest(secondUser.getFullName(), firstUser.getUsername(), firstUser.getPassword())));
        assertThrows(IllegalArgumentException.class, () -> userService.create(null));
    }

    @Test
    void get() {
        int realId = firstUser.getId();
        int notRealId = 2;

        String realLogin = firstUser.getUsername();
        String notRealLogin = "notRealLogin";

        UserResponse expectedResponse = new UserResponse(realId, firstUser.getFullName());

        when(userDao.findById(realId)).thenReturn(Optional.of(firstUser));
        when(userDao.findById(notRealId)).thenReturn(Optional.empty());
        when(userDao.findByUsername(realLogin)).thenReturn(Optional.of(firstUser));
        when(userDao.findByUsername(notRealLogin)).thenReturn(Optional.empty());

        Optional<UserResponse> actualResponse = userService.findById(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());
        assertFalse(userService.findById(notRealId).isPresent());

        actualResponse = userService.findByUsername(realLogin);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(userService.findByUsername(notRealLogin).isPresent());
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

        UserResponse firstResponse = new UserResponse(firstUser.getId(), firstUser.getFullName());
        UserResponse secondResponse = new UserResponse(secondUser.getId(), secondUser.getFullName());
        UserResponse thirdResponse = new UserResponse(thirdUser.getId(), thirdUser.getFullName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), userService.findAll(firstPageRequest).getContent());
        assertEquals(Collections.singletonList(thirdResponse), userService.findAll(secondPageRequest).getContent());
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), userService.findAll(thirdPageRequest).getContent());

        assertThrows(IllegalArgumentException.class, () -> userService.findAll(PageRequest.of(-1, 2)));
        assertThrows(IllegalArgumentException.class, () -> userService.findAll(PageRequest.of(1, -1)));
    }
}