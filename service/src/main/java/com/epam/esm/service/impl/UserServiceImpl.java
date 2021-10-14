package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.EntityExistsException;
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
public class UserServiceImpl implements UserService {
    private ModelMapper mapper;
    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, ModelMapper mapper) {
        this.userDao = userDao;
        this.mapper = mapper;
    }

    /**
     * Retrieves all users.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of users.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<UserResponse> findAll(int pageNumber, int pageSize) {
        try {
            return userDao.findAll(pageNumber, pageSize).stream()
                    .map(user -> mapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieves user with given id.
     *
     * @param id id of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<UserResponse> find(int id) {
        return userDao.find(id).map(user -> mapper.map(user, UserResponse.class));
    }

    /**
     * Retrieves user with given login.
     *
     * @param login login of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     * @throws IllegalArgumentException if login is null.
     */
    @Override
    public Optional<UserResponse> find(String login) {
        Assert.notNull(login, "User login can't be null");
        return userDao.get(login).map(user -> mapper.map(user, UserResponse.class));
    }

    /**
     * Returns count of users.
     *
     * @return count of users.
     */
    @Override
    public long getCount() {
        return userDao.getCount();
    }

    /**
     * Creates new user from given {@link CreateUserRequest}.
     *
     * @param user user to create.
     * @return created user.
     * @throws IllegalArgumentException if user is null.
     * @throws InvalidEntityException   if user is invalid
     * @throws EntityExistsException    if user with the same name already exists.
     */
    @Override
    public UserResponse create(CreateUserRequest user) {
        User userToCreate = mapper.map(user, User.class);
        if (userDao.get(userToCreate.getLogin()).isPresent()) {
            throw new EntityExistsException("login=" + userToCreate.getLogin(), ErrorCode.USER_EXISTS);
        }
        try {
            userDao.create(userToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_USER);
        }
        return mapper.map(userToCreate, UserResponse.class);
    }
}
