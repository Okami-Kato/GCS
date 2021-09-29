package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.service.dto.response.UserOrderResponse;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.ServiceException;
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
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserOrderServiceImplTest {

    @Autowired
    private ModelMapper mapper;
    private UserOrderDao userOrderDao;
    private UserDao userDao;
    private CertificateDao certificateDao;

    private UserOrderService userOrderService;

    private User firstUser = new User("first", "test", "first", "password");
    private User secondUser = new User("second", "test", "second", "password");

    private Certificate firstCertificate = new Certificate("first", "first", 5, 1);
    private Certificate secondCertificate = new Certificate("second", "second", 15, 3);
    private Certificate thirdCertificate = new Certificate("third", "third", 25, 10);

    private UserOrder firstOrder = new UserOrder(firstUser, firstCertificate, firstCertificate.getPrice());
    private UserOrder secondOrder = new UserOrder(firstUser, thirdCertificate, thirdCertificate.getPrice());
    private UserOrder thirdOrder = new UserOrder(secondUser, secondCertificate, secondCertificate.getPrice());
    private UserOrder forthOrder = new UserOrder(secondUser, thirdCertificate, thirdCertificate.getPrice());

    @BeforeAll
    void init() {
        userDao = mock(UserDao.class);
        certificateDao = mock(CertificateDao.class);
        userOrderDao = mock(UserOrderDao.class);
        userOrderService = new UserOrderServiceImpl(mapper, userOrderDao, userDao, certificateDao);

        firstUser.setId(1);
        secondUser.setId(2);

        firstCertificate.setId(1);
        secondCertificate.setId(2);
        thirdCertificate.setId(3);

        firstOrder.setId(1);
        secondOrder.setId(2);
        thirdOrder.setId(3);
        forthOrder.setId(4);
    }

    @Test
    void create() {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((UserOrder) args[0]).setId(firstOrder.getId());
            return null;
        }).doThrow(DataIntegrityViolationException.class)
                .when(userOrderDao).create(new UserOrder(firstUser, firstCertificate, firstCertificate.getPrice()));

        int notRealId = 10;
        when(userDao.get(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userDao.get(notRealId)).thenReturn(Optional.empty());
        when(certificateDao.get(firstCertificate.getId())).thenReturn(Optional.of(firstCertificate));
        when(certificateDao.get(notRealId)).thenReturn(Optional.empty());

        UserOrderResponse actualResponse = userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), firstCertificate.getId()));
        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName()),
                new CertificateItem(firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(), new HashSet<>()),
                firstCertificate.getPrice(), null);

        assertEquals(expectedResponse, actualResponse);

        assertThrows(ServiceException.class, () -> userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), firstCertificate.getId())));
        assertThrows(ServiceException.class, () -> userOrderService.create(new CreateUserOrderRequest(notRealId, firstCertificate.getId())));
        assertThrows(ServiceException.class, () -> userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), notRealId)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.create(new CreateUserOrderRequest(null, firstCertificate.getId())));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), null)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.create(null));
    }

    @Test
    void get() {
        int realId = 1;
        int notRealId = 2;

        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), new UserResponse(firstUser.getId(), firstUser.getFirstName(), firstUser.getLastName()),
                new CertificateItem(firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(), new HashSet<>()),
                firstCertificate.getPrice(), null);

        when(userOrderDao.get(realId)).thenReturn(Optional.of(firstOrder));
        when(userOrderDao.get(notRealId)).thenReturn(Optional.empty());

        Optional<UserOrderResponse> actualResponse = userOrderService.get(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(userOrderService.get(notRealId).isPresent());
    }

    @Test
    void getAll() {
        when(userOrderDao.getAll(1, 2)).thenReturn(Arrays.asList(firstOrder, secondOrder));
        when(userOrderDao.getAll(2, 2)).thenReturn(Arrays.asList(thirdOrder, forthOrder));
        when(userOrderDao.getAll(1, 3)).thenReturn(Arrays.asList(firstOrder, secondOrder, thirdOrder));
        when(userOrderDao.getAll(intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(userOrderDao.getAll(anyInt(), intThat(i -> i < 0))).thenThrow(InvalidDataAccessApiUsageException.class);

        UserOrderItem firstItem = new UserOrderItem(
                firstOrder.getId(), firstOrder.getUser().getId(), firstOrder.getCertificate().getId(), firstOrder.getCost());
        UserOrderItem secondItem = new UserOrderItem(
                secondOrder.getId(), secondOrder.getUser().getId(), secondOrder.getCertificate().getId(), secondOrder.getCost());
        UserOrderItem thirdItem = new UserOrderItem(
                thirdOrder.getId(), thirdOrder.getUser().getId(), thirdOrder.getCertificate().getId(), thirdOrder.getCost());
        UserOrderItem forthItem = new UserOrderItem(
                forthOrder.getId(), forthOrder.getUser().getId(), forthOrder.getCertificate().getId(), forthOrder.getCost());

        assertEquals(Arrays.asList(firstItem, secondItem), userOrderService.getAll(1, 2));
        assertEquals(Arrays.asList(thirdItem, forthItem), userOrderService.getAll(2, 2));
        assertEquals(Arrays.asList(firstItem, secondItem, thirdItem), userOrderService.getAll(1, 3));
        assertThrows(ServiceException.class, () -> userOrderService.getAll(-1, 2));
        assertThrows(ServiceException.class, () -> userOrderService.getAll(1, -1));

    }
}