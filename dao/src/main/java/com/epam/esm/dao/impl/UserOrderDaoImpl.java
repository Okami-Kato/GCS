package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.UserOrder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserOrderDaoImpl implements UserOrderDao {
    private final String GET_USER_ORDER_BY_ID = "SELECT uo FROM UserOrder uo LEFT JOIN FETCH uo.certificate LEFT JOIN FETCH uo.user WHERE uo.id=:id";
    private final String GET_ALL_USER_ORDERS = "SELECT uo FROM UserOrder uo";
    private final String GET_ALL_BY_USER_ID = "SELECT uo FROM UserOrder uo WHERE uo.user.id=:userId";
    private final String GET_ALL_BY_CERTIFICATE_ID = "SELECT uo FROM UserOrder uo WHERE uo.certificate.id=:certificateId";
    private final String GET_COUNT = "SELECT COUNT(uo) FROM UserOrder uo";

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<UserOrder> get(Integer id) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_USER_ORDER_BY_ID, UserOrder.class);
        try {
            return Optional.of(query.setParameter("id", id).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserOrder> getAll(int pageNumber, int pageSize) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_USER_ORDERS, UserOrder.class);
        return query.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<UserOrder> getAllByUserId(int pageNumber, int pageSize, int userId) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_BY_USER_ID, UserOrder.class);
        return query.setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<UserOrder> getAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        TypedQuery<UserOrder> query = manager.createQuery(GET_ALL_BY_CERTIFICATE_ID, UserOrder.class);
        return query.setParameter("certificateId", certificateId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    @Override
    public void create(UserOrder userOrder) {
        manager.persist(userOrder);
    }

    @Override
    public UserOrder update(UserOrder userOrder) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserOrderDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Method delete() isn't supported in UserDaoImpl");
    }
}
