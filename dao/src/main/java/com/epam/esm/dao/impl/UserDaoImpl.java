package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<User> get(Integer id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    @Override
    public List<User> getAll(int pageNumber, int pageSize) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT u.id FROM User u", Integer.class);
        List<Integer> userIds = idQuery
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        TypedQuery<User> userQuery = manager.createQuery("SELECT u FROM User u WHERE u.id in (:ids) ORDER BY u.id", User.class);
        return userQuery
                .setParameter("ids", userIds)
                .getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
    }

    @Override
    public void create(User user) {
        throw new UnsupportedOperationException("Method create() isn't supported in UserDaoImpl");
    }

    @Override
    public void update(User user) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Method delete() isn't supported in UserDaoImpl");
    }
}
