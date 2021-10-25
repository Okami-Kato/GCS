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
import com.epam.esm.service.exception.EntityNotFoundException;
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
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        UserOrder order = new UserOrder(firstOrder.getUser(), firstOrder.getCertificate(), firstOrder.getCost());
        when(userOrderDao.save(order)).thenReturn(firstOrder);

        int notRealId = 10;
        when(userDao.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userDao.findById(notRealId)).thenReturn(Optional.empty());
        when(certificateDao.findById(firstCertificate.getId())).thenReturn(Optional.of(firstCertificate));
        when(certificateDao.findById(notRealId)).thenReturn(Optional.empty());

        UserOrderResponse actualResponse = userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), firstCertificate.getId()));
        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), firstUser.getId(),
                new CertificateItem(firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(), new HashSet<>()),
                firstCertificate.getPrice(), null);

        assertEquals(expectedResponse, actualResponse);

        assertThrows(EntityNotFoundException.class, () -> userOrderService.create(new CreateUserOrderRequest(notRealId, firstCertificate.getId())));
        assertThrows(EntityNotFoundException.class, () -> userOrderService.create(new CreateUserOrderRequest(firstUser.getId(), notRealId)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.create(null));
    }

    @Test
    void get() {
        int realId = 1;
        int notRealId = 2;

        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), firstUser.getId(),
                new CertificateItem(firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(), new HashSet<>()),
                firstCertificate.getPrice(), null);

        when(userOrderDao.findById(realId)).thenReturn(Optional.of(firstOrder));
        when(userOrderDao.findById(notRealId)).thenReturn(Optional.empty());

        Optional<UserOrderResponse> actualResponse = userOrderService.findById(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(userOrderService.findById(notRealId).isPresent());
    }

    @Test
    void read() {
        PageRequest firstPageRequest = PageRequest.of(0, 2);
        PageRequest secondPageRequest = PageRequest.of(1, 2);
        PageRequest thirdPageRequest = PageRequest.of(0, 3);

        when(userOrderDao.findAll(firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstOrder, secondOrder), firstPageRequest, 4));
        when(userOrderDao.findAll(secondPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(thirdOrder, forthOrder), secondPageRequest, 4));
        when(userOrderDao.findAll(thirdPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstOrder, secondOrder, thirdOrder), thirdPageRequest, 4));

        when(userOrderDao.findAllByUserId(firstUser.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstOrder, secondOrder), firstPageRequest, 2));
        when(userOrderDao.findAllByUserId(secondUser.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(thirdOrder, forthOrder), firstPageRequest, 2));

        when(userOrderDao.findAllByCertificateId(firstCertificate.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Collections.singletonList(firstOrder), firstPageRequest, 1));
        when(userOrderDao.findAllByCertificateId(thirdCertificate.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(secondOrder, forthOrder), firstPageRequest, 2));

        UserOrderItem firstItem = new UserOrderItem(
                firstOrder.getId(), firstOrder.getUser().getId(), firstOrder.getCertificate().getId(), firstOrder.getCost());
        UserOrderItem secondItem = new UserOrderItem(
                secondOrder.getId(), secondOrder.getUser().getId(), secondOrder.getCertificate().getId(), secondOrder.getCost());
        UserOrderItem thirdItem = new UserOrderItem(
                thirdOrder.getId(), thirdOrder.getUser().getId(), thirdOrder.getCertificate().getId(), thirdOrder.getCost());
        UserOrderItem forthItem = new UserOrderItem(
                forthOrder.getId(), forthOrder.getUser().getId(), forthOrder.getCertificate().getId(), forthOrder.getCost());

        assertEquals(Arrays.asList(firstItem, secondItem), userOrderService.findAll(firstPageRequest).getContent());
        assertEquals(Arrays.asList(thirdItem, forthItem), userOrderService.findAll(secondPageRequest).getContent());
        assertEquals(Arrays.asList(firstItem, secondItem, thirdItem), userOrderService.findAll(thirdPageRequest).getContent());
        assertThrows(IllegalArgumentException.class, () -> userOrderService.findAll(PageRequest.of(-1, 1)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.findAll(PageRequest.of(1, -1)));

        when(userDao.existsById(firstUser.getId())).thenReturn(true);
        when(userDao.existsById(secondUser.getId())).thenReturn(true);
        assertEquals(Arrays.asList(firstItem, secondItem),
                userOrderService.findAllByUserId(firstUser.getId(), firstPageRequest).getContent());
        assertEquals(Arrays.asList(thirdItem, forthItem),
                userOrderService.findAllByUserId(secondUser.getId(), firstPageRequest).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByUserId(firstUser.getId(), PageRequest.of(-1, 1)).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByUserId(firstUser.getId(), PageRequest.of(1, -1)).getContent());

        when(certificateDao.existsById(firstCertificate.getId())).thenReturn(true);
        when(certificateDao.existsById(thirdCertificate.getId())).thenReturn(true);
        assertEquals(Collections.singletonList(firstItem),
                userOrderService.findAllByCertificateId(firstCertificate.getId(), firstPageRequest).getContent());
        assertEquals(Arrays.asList(secondItem, forthItem),
                userOrderService.findAllByCertificateId(thirdCertificate.getId(), firstPageRequest).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByCertificateId(firstCertificate.getId(), PageRequest.of(-1, 1)).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByCertificateId(firstCertificate.getId(), PageRequest.of(1, -1)).getContent());
    }
}