package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.UserOrder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserOrderDaoImpl implements UserOrderDao {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<UserOrder> get(Integer id) {
        return Optional.ofNullable(manager.find(UserOrder.class, id));
    }

    @Override
    public List<UserOrder> getAll(int pageNumber, int pageSize) {
        TypedQuery<UserOrder> idQuery = manager.createQuery("SELECT o FROM UserOrder o", UserOrder.class);
        return idQuery.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<UserOrder> getAll(int pageNumber, int pageSize, int userId) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT uo.id FROM UserOrder uo WHERE uo.user.id=:userId", Integer.class);
        List<Integer> userOrderIds = idQuery
                .setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        TypedQuery<UserOrder> tagQuery = manager.createQuery("SELECT uo FROM UserOrder uo WHERE uo.id in (:ids) ORDER BY uo.id", UserOrder.class);
        return tagQuery
                .setParameter("ids", userOrderIds)
                .getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery("SELECT COUNT(o) FROM UserOrder o", Long.class).getSingleResult();
    }

    @Override
    public void create(UserOrder userOrder) {
        manager.persist(userOrder);
    }

    @Override
    public void update(UserOrder userOrder) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserOrderDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Method delete() isn't supported in UserDaoImpl");
    }
}
