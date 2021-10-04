package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {
    private final String GET_ALL_USERS = "SELECT u FROM User u";
    private final String GET_COUNT = "SELECT COUNT(u) FROM User u";

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<User> get(Integer id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    @Override
    public List<User> getAll(int pageNumber, int pageSize) {
        TypedQuery<User> query = manager.createQuery(GET_ALL_USERS, User.class);
        return query.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    @Override
    public void create(User user) {
        throw new UnsupportedOperationException("Method create() isn't supported in UserDaoImpl");
    }

    @Override
    public User update(User user) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Method delete() isn't supported in UserDaoImpl");
    }
}
