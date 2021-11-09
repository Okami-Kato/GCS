package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserOrderServiceImplTest {

    @Autowired
    private ModelMapper mapper;
    private UserOrderDao userOrderDao;
    private CertificateDao certificateDao;

    private UserOrderService userOrderService;

    private final String firstUserId = "first user";
    private final String secondUserId = "second user";

    private final Certificate firstCertificate = Certificate.builder()
            .id(1)
            .name("first name")
            .description("first description")
            .price(5)
            .duration(1)
            .build();
    private final Certificate secondCertificate = Certificate.builder()
            .id(2)
            .name("second name")
            .description("second description")
            .price(15)
            .duration(3)
            .build();
    private final Certificate thirdCertificate = Certificate.builder()
            .id(3)
            .name("third name")
            .description("third description")
            .price(25)
            .duration(10)
            .build();

    private final UserOrder firstOrder = new UserOrder(1, firstUserId, firstCertificate, firstCertificate.getPrice());
    private final UserOrder secondOrder = new UserOrder(2, firstUserId, thirdCertificate, thirdCertificate.getPrice());
    private final UserOrder thirdOrder = new UserOrder(3, secondUserId, secondCertificate, secondCertificate.getPrice());
    private final UserOrder forthOrder = new UserOrder(4, secondUserId, thirdCertificate, thirdCertificate.getPrice());

    @BeforeAll
    void init() {
        certificateDao = mock(CertificateDao.class);
        userOrderDao = mock(UserOrderDao.class);
        userOrderService = new UserOrderServiceImpl(mapper, userOrderDao, certificateDao);
    }

    @Test
    void create() {
        UserOrder order = UserOrder.builder()
                .userId(firstOrder.getUserId())
                .certificate(firstOrder.getCertificate())
                .cost(firstOrder.getCost())
                .build();
        when(userOrderDao.save(any())).thenReturn(firstOrder);

        int notRealId = 10;
        when(certificateDao.findById(firstCertificate.getId())).thenReturn(Optional.of(firstCertificate));
        when(certificateDao.findById(notRealId)).thenReturn(Optional.empty());

        UserOrderResponse actualResponse = userOrderService.create(new CreateUserOrderRequest(firstUserId, firstCertificate.getId()));
        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), firstUserId,
                new CertificateItem(firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(), new HashSet<>()),
                firstCertificate.getPrice(), null);

        assertEquals(expectedResponse, actualResponse);

        assertThrows(EntityNotFoundException.class, () -> userOrderService.create(new CreateUserOrderRequest("not real id", firstCertificate.getId())));
        assertThrows(EntityNotFoundException.class, () -> userOrderService.create(new CreateUserOrderRequest(firstUserId, notRealId)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.create(null));
    }

    @Test
    void get() {
        int realId = 1;
        int notRealId = 2;

        UserOrderResponse expectedResponse = new UserOrderResponse(
                firstOrder.getId(), firstUserId,
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

        when(userOrderDao.findAllByUserId(firstUserId, firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstOrder, secondOrder), firstPageRequest, 2));
        when(userOrderDao.findAllByUserId(secondUserId, firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(thirdOrder, forthOrder), firstPageRequest, 2));

        when(userOrderDao.findAllByCertificateId(firstCertificate.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Collections.singletonList(firstOrder), firstPageRequest, 1));
        when(userOrderDao.findAllByCertificateId(thirdCertificate.getId(), firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(secondOrder, forthOrder), firstPageRequest, 2));

        UserOrderItem firstItem = new UserOrderItem(
                firstOrder.getId(), firstOrder.getUserId(), firstOrder.getCertificate().getId(), firstOrder.getCost());
        UserOrderItem secondItem = new UserOrderItem(
                secondOrder.getId(), secondOrder.getUserId(), secondOrder.getCertificate().getId(), secondOrder.getCost());
        UserOrderItem thirdItem = new UserOrderItem(
                thirdOrder.getId(), thirdOrder.getUserId(), thirdOrder.getCertificate().getId(), thirdOrder.getCost());
        UserOrderItem forthItem = new UserOrderItem(
                forthOrder.getId(), forthOrder.getUserId(), forthOrder.getCertificate().getId(), forthOrder.getCost());

        assertEquals(Arrays.asList(firstItem, secondItem), userOrderService.findAll(firstPageRequest).getContent());
        assertEquals(Arrays.asList(thirdItem, forthItem), userOrderService.findAll(secondPageRequest).getContent());
        assertEquals(Arrays.asList(firstItem, secondItem, thirdItem), userOrderService.findAll(thirdPageRequest).getContent());
        assertThrows(IllegalArgumentException.class, () -> userOrderService.findAll(PageRequest.of(-1, 1)));
        assertThrows(IllegalArgumentException.class, () -> userOrderService.findAll(PageRequest.of(1, -1)));

        assertEquals(Arrays.asList(firstItem, secondItem),
                userOrderService.findAllByUserId(firstUserId, firstPageRequest).getContent());
        assertEquals(Arrays.asList(thirdItem, forthItem),
                userOrderService.findAllByUserId(secondUserId, firstPageRequest).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByUserId(firstUserId, PageRequest.of(-1, 1)).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> userOrderService.findAllByUserId(firstUserId, PageRequest.of(1, -1)).getContent());

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