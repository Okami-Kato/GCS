package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
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
public class UserDaoImpl implements UserDao {
    private final String GET_USER_BY_LOGIN = "SELECT u FROM User u WHERE u.login=:login";
    private final String GET_ALL_USERS = "SELECT u FROM User u";
    private final String GET_COUNT = "SELECT COUNT(u) FROM User u";

    @PersistenceContext
    private EntityManager manager;

    /**
     * Retrieves user with given id.
     *
     * @param id id of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if id is null.
     */
    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    /**
     * Retrieves user with given login.
     *
     * @param login login of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if login is null.
     */
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

    /**
     * Retrieves all users.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of certificates.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<User> findAll(int pageNumber, int pageSize) {
        TypedQuery<User> query = manager.createQuery(GET_ALL_USERS, User.class);
        return query.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Returns count of users.
     *
     * @return count of users.
     */
    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    /**
     * Creates user.
     *
     * @param user user to create.
     * @throws InvalidDataAccessApiUsageException if given user already exists, or if given user is null.
     * @throws DataIntegrityViolationException    if given user is invalid, or if user with
     *                                            the same login already exists.
     */
    @Override
    public void create(User user) {
        manager.persist(user);
        manager.flush();
    }

    @Override
    public User update(User user) {
        throw new UnsupportedOperationException("Method update() isn't supported in UserDaoImpl");
    }

    /**
     * Deletes user with given id.
     *
     * @param id id of user to delete.
     * @throws InvalidDataAccessApiUsageException if given id is null.
     * @throws JpaObjectRetrievalFailureException if user with given id doesn't exist.
     */
    @Override
    public void delete(Integer id) {
        Optional<User> user = find(id);
        if (user.isPresent()) {
            manager.remove(user.get());
        } else {
            throw new EntityNotFoundException(String.format("User not found (id=%s)", id));
        }
    }
}
