package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.service.dto.response.UserOrderResponse;
import com.epam.esm.service.exception.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.service.util.ServiceUtil.executeDaoCall;

@Service
@Transactional
public class UserOrderServiceImpl implements UserOrderService {
    private ModelMapper mapper;
    private UserOrderDao userOrderDao;
    private UserDao userDao;
    private CertificateDao certificateDao;

    @Autowired
    public UserOrderServiceImpl(ModelMapper mapper, UserOrderDao userOrderDao, UserDao userDao, CertificateDao certificateDao) {
        this.mapper = mapper;
        this.userOrderDao = userOrderDao;
        this.userDao = userDao;
        this.certificateDao = certificateDao;
    }

    @Override
    public List<UserOrderItem> getAll(int pageNumber, int pageSize) {
        return executeDaoCall(() -> userOrderDao.getAll(pageNumber, pageSize).stream()
                .map(order -> mapper.map(order, UserOrderItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<UserOrderItem> findAllByUserId(int pageNumber, int pageSize, int userId) {
        return executeDaoCall(() -> userOrderDao.findAllByUserId(pageNumber, pageSize, userId).stream()
                .map(order -> mapper.map(order, UserOrderItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public List<UserOrderItem> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        return executeDaoCall(() -> userOrderDao.findAllByCertificateId(pageNumber, pageSize, certificateId).stream()
                .map(order -> mapper.map(order, UserOrderItem.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<UserOrderResponse> get(int id) {
        return userOrderDao.get(id).map(order -> mapper.map(order, UserOrderResponse.class));
    }

    @Override
    public long getCount() {
        return userOrderDao.getCount();
    }

    @Override
    public UserOrderResponse create(CreateUserOrderRequest userOrder) {
        if (userOrder == null) {
            throw new IllegalArgumentException("Passed UserOrder can't be null");
        }
        Optional<User> user = executeDaoCall(() -> userDao.get(userOrder.getUserId()));
        Optional<Certificate> certificate = executeDaoCall(() -> certificateDao.get(userOrder.getCertificateId()));

        UserOrder orderToCreate = new UserOrder(
                user.orElseThrow(() -> new ServiceException(
                        String.format("User not found (%s)", "id=" + userOrder.getUserId()))),
                certificate.orElseThrow(() -> new ServiceException(
                        String.format("Certificate not found (%s)", "id=" + userOrder.getUserId()))),
                certificate.get().getPrice()
        );

        executeDaoCall(() -> userOrderDao.create(orderToCreate));
        return mapper.map(orderToCreate, UserOrderResponse.class);
    }
}
