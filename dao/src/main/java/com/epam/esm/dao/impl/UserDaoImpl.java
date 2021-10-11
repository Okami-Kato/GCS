package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
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
public class UserDaoImpl implements UserDao {
    private final String GET_USER_BY_LOGIN = "SELECT u FROM User u WHERE u.login=:login";
    private final String GET_ALL_USERS = "SELECT u FROM User u";
    private final String GET_COUNT = "SELECT COUNT(u) FROM User u";

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<User> get(Integer id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    @Override
    public Optional<User> get(String login) {
        if (login == null) {
            throw new IllegalArgumentException("User login can't be null");
        }
        TypedQuery<User> query = manager.createQuery(GET_USER_BY_LOGIN, User.class);
        try {
            return Optional.of(query.setParameter("login", login).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
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
        manager.persist(user);
        manager.flush();
    }

    @Override
    public User update(User user) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        Optional<User> user = get(id);
        if (user.isPresent()) {
            manager.remove(user.get());
        } else {
            throw new IllegalArgumentException(String.format("User wasn't found (%s)", "id=" + id));
        }
    }
}
