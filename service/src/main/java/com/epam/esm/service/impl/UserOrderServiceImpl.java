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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

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
     * @param pageable pagination restrictions.
     * @return page of orders.
     */
    @Override
    public Page<UserOrderItem> findAll(Pageable pageable) {
        return userOrderDao.findAll(pageable)
                .map(order -> mapper.map(order, UserOrderItem.class));
    }

    /**
     * Retrieves all orders of user with given id.
     *
     * @param pageable pagination restrictions.
     * @param userId   id of user.
     * @return page of found orders.
     * @throws EntityNotFoundException if user with given id wasn't found.
     */
    @Override
    public Page<UserOrderItem> findAllByUserId(int userId, Pageable pageable) {
        if (!userDao.existsById(userId)) {
            throw new EntityNotFoundException("id=" + userId, ErrorCode.USER_NOT_FOUND);
        }
        return userOrderDao.findAllByUserId(userId, pageable)
                .map(order -> mapper.map(order, UserOrderItem.class));
    }

    /**
     * Retrieves all orders on certificate with given id.
     *
     * @param pageable      pagination restrictions.
     * @param certificateId id of certificate.
     * @return page of found orders.
     * @throws EntityNotFoundException if certificate with given id wasn't found.
     */
    @Override
    public Page<UserOrderItem> findAllByCertificateId(int certificateId, Pageable pageable) {
        if (!certificateDao.existsById(certificateId)) {
            throw new EntityNotFoundException("id=" + certificateId, ErrorCode.CERTIFICATE_NOT_FOUND);
        }
        return userOrderDao.findAllByCertificateId(certificateId, pageable)
                .map(order -> mapper.map(order, UserOrderItem.class));
    }

    /**
     * Retrieves order with given id.
     *
     * @param id id of order.
     * @return Optional with certificate, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<UserOrderResponse> findById(int id) {
        return userOrderDao.findById(id).map(order -> mapper.map(order, UserOrderResponse.class));
    }

    /**
     * Returns count of orders.
     *
     * @return count of orders.
     */
    @Override
    public long getCount() {
        return userOrderDao.count();
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
        Optional<User> user = userDao.findById(userOrder.getUserId());
        Optional<Certificate> certificate = certificateDao.findById(userOrder.getCertificateId());

        UserOrder orderToCreate = new UserOrder(
                user.orElseThrow(() -> new EntityNotFoundException("id=" + userOrder.getUserId(),
                        ErrorCode.USER_NOT_FOUND)),
                certificate.orElseThrow(() -> new EntityNotFoundException("id=" + userOrder.getCertificateId(),
                        ErrorCode.CERTIFICATE_NOT_FOUND)),
                certificate.get().getPrice()
        );
        try {
            UserOrder created = userOrderDao.save(orderToCreate);
            return mapper.map(created, UserOrderResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_USER_ORDER);
        }
    }
}
