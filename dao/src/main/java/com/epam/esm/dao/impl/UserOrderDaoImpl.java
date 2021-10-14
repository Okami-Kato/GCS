package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.UserOrder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserOrderDaoImpl implements UserOrderDao {
    private final String GET_USER_ORDER_BY_ID = "SELECT uo FROM UserOrder uo LEFT JOIN FETCH uo.certificate LEFT JOIN FETCH uo.user WHERE uo.id=:id";
    private final String GET_ALL_USER_ORDERS = "SELECT uo FROM UserOrder uo LEFT JOIN FETCH uo.certificate LEFT JOIN FETCH uo.user";
    private final String GET_ALL_BY_USER_ID = "SELECT uo FROM UserOrder uo LEFT JOIN FETCH uo.certificate LEFT JOIN FETCH uo.user WHERE uo.user.id=:userId";
    private final String GET_ALL_BY_CERTIFICATE_ID = "SELECT uo FROM UserOrder uo LEFT JOIN FETCH uo.certificate LEFT JOIN FETCH uo.user WHERE uo.certificate.id=:certificateId";
    private final String GET_COUNT = "SELECT COUNT(uo) FROM UserOrder uo";

    @PersistenceContext
    private EntityManager manager;

    /**
     * Retrieves order with given id.
     *
     * @param id id of order.
     * @return Optional with order, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if id is null.
     */
    @Override
    public Optional<UserOrder> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("UserOrder id can't be null");
        }
        TypedQuery<UserOrder> query = manager.createQuery(GET_USER_ORDER_BY_ID, UserOrder.class);
        try {
            return Optional.of(query.setParameter("id", id).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all orders.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of orders.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrder> findAll(int pageNumber, int pageSize) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_USER_ORDERS, UserOrder.class);
        return query.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Retrieves all orders of user with given id.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @param userId     id of user.
     * @return list of found orders.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrder> findAllByUserId(int pageNumber, int pageSize, int userId) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_BY_USER_ID, UserOrder.class);
        return query.setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Retrieves all orders on certificate with given id.
     *
     * @param pageNumber    number of page (starts from 1).
     * @param pageSize      size of page.
     * @param certificateId id of certificate.
     * @return list of found orders.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserOrder> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_BY_CERTIFICATE_ID, UserOrder.class);
        return query.setParameter("certificateId", certificateId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Returns count of orders.
     *
     * @return count of orders.
     */
    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    /**
     * Creates order.
     *
     * @param userOrder order to create.
     * @throws InvalidDataAccessApiUsageException if given order already exists, or if given order is null.
     * @throws DataIntegrityViolationException    if given order is invalid.
     */
    @Override
    public void create(UserOrder userOrder) {
        manager.persist(userOrder);
    }

    @Override
    public UserOrder update(UserOrder userOrder) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserOrderDaoImpl");
    }

    /**
     * Deletes order with given id.
     *
     * @param id id of order to delete.
     * @throws JpaObjectRetrievalFailureException if order with given id doesn't exist.
     */
    @Override
    public void delete(Integer id) {
        Optional<UserOrder> order = find(id);
        if (order.isPresent()) {
            manager.remove(order.get());
        } else {
            throw new EntityNotFoundException(String.format("UserOrder not found (id=%s)", id));
        }
    }
}
