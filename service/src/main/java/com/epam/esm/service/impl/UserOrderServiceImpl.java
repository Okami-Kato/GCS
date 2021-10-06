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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try {
            return userOrderDao.getAll(pageNumber, pageSize).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<UserOrderItem> getAllByUserId(int pageNumber, int pageSize, int userId) {
        try {
            return userOrderDao.getAllByUserId(pageNumber, pageSize, userId).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<UserOrderItem> getAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        try {
            return userOrderDao.getAllByCertificateId(pageNumber, pageSize, certificateId).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
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
        if (userOrder == null){
            throw new IllegalArgumentException("Passed UserOrder can't be null");
        }
        if (userOrder.getUserId() == null) {
            throw new IllegalArgumentException("User id can't be null");
        }
        if (userOrder.getCertificateId() == null) {
            throw new IllegalArgumentException("Certificate id can't be null");
        }

        Optional<User> user = userDao.get(userOrder.getUserId());
        if (!user.isPresent()) {
            throw new ServiceException(String.format("User not found (%s)", "id=" + userOrder.getUserId()));
        }

        Optional<Certificate> certificate = certificateDao.get(userOrder.getCertificateId());
        if (!certificate.isPresent()) {
            throw new ServiceException(String.format("Certificate not found (%s)", "id=" + userOrder.getUserId()));
        }

        UserOrder orderToCreate = new UserOrder(user.get(), certificate.get(), certificate.get().getPrice());

        try {
            userOrderDao.create(orderToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(e);
        }
        return mapper.map(orderToCreate, UserOrderResponse.class);
    }
}
