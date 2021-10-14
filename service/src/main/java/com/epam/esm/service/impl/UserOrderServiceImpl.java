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
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    /**
     * Retrieves all orders.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of orders.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrderItem> getAll(int pageNumber, int pageSize) {
        try {
            return userOrderDao.getAll(pageNumber, pageSize).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves all orders of user with given id.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @param userId     id of user.
     * @return list of found orders.
     * @throws EntityNotFoundException  if user with given id wasn't found.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrderItem> findAllByUserId(int pageNumber, int pageSize, int userId) {
        if (!userDao.get(userId).isPresent()) {
            throw new EntityNotFoundException("id=" + userId, ErrorCode.USER_NOT_FOUND);
        }
        try {
            return userOrderDao.findAllByUserId(pageNumber, pageSize, userId).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves all orders on certificate with given id.
     *
     * @param pageNumber    number of page (starts from 1).
     * @param pageSize      size of page.
     * @param certificateId id of certificate.
     * @return list of found orders.
     * @throws EntityNotFoundException  if certificate with given id wasn't found.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrderItem> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        if (!certificateDao.get(certificateId).isPresent()) {
            throw new EntityNotFoundException("id=" + certificateId, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
        try {
            return userOrderDao.findAllByCertificateId(pageNumber, pageSize, certificateId).stream()
                    .map(order -> mapper.map(order, UserOrderItem.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves order with given id.
     *
     * @param id id of order.
     * @return Optional with certificate, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<UserOrderResponse> get(int id) {
        return userOrderDao.get(id).map(order -> mapper.map(order, UserOrderResponse.class));
    }

    /**
     * Returns count of orders.
     *
     * @return count of orders.
     */
    @Override
    public long getCount() {
        return userOrderDao.getCount();
    }

    /**
     * Creates new order from given {@link CreateUserOrderRequest}.
     *
     * @param userOrder order to be created.
     * @return created order.
     * @throws IllegalArgumentException if userOrder is null.
     * @throws InvalidEntityException   if order is invalid.
     */
    @Override
    public UserOrderResponse create(CreateUserOrderRequest userOrder) {
        Assert.notNull(userOrder, "userOrder can't be null");
        Optional<User> user = userDao.get(userOrder.getUserId());
        Optional<Certificate> certificate = certificateDao.get(userOrder.getCertificateId());

        UserOrder orderToCreate = new UserOrder(
                user.orElseThrow(() -> new EntityNotFoundException("id=" + userOrder.getUserId(),
                        ErrorCode.USER_NOT_FOUND)),
                certificate.orElseThrow(() -> new EntityNotFoundException("id=" + userOrder.getCertificateId(),
                        ErrorCode.CERTIFICATE_NOT_FOUND)),
                certificate.get().getPrice()
        );
        try {
            userOrderDao.create(orderToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_USER_ORDER);
        }
        return mapper.map(orderToCreate, UserOrderResponse.class);
    }
}
