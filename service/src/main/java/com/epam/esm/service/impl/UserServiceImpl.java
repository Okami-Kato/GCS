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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param pageable pagination restrictions.
     * @return page of users.
     */
    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        return userDao.findAll(pageable)
                .map(user -> mapper.map(user, UserResponse.class));
    }

    /**
     * Retrieves user with given id.
     *
     * @param id id of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     */
    @Override
    public Optional<UserResponse> findById(int id) {
        return userDao.findById(id).map(user -> mapper.map(user, UserResponse.class));
    }

    /**
     * Retrieves user with given login.
     *
     * @param login login of user.
     * @return Optional with user, if it was found, otherwise an empty Optional.
     * @throws IllegalArgumentException if login is null.
     */
    @Override
    public Optional<UserResponse> findByLogin(String login) {
        Assert.notNull(login, "User login can't be null");
        return userDao.findByLogin(login).map(user -> mapper.map(user, UserResponse.class));
    }

    /**
     * Retrieves users with the highest cost.
     *
     * @return list of users with the highest cost.
     */
    @Override
    public List<UserResponse> findUsersWithTheHighestCost() {
        return userDao.findUsersWithTheHighestCost().stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns count of users.
     *
     * @return count of users.
     */
    @Override
    public long getCount() {
        return userDao.count();
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
        if (userDao.existsByLogin(userToCreate.getLogin())) {
            throw new EntityExistsException("login=" + userToCreate.getLogin(), ErrorCode.USER_EXISTS);
        }
        try {
            User created = userDao.save(userToCreate);
            return mapper.map(created, UserResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException(e.getMessage(), ErrorCode.INVALID_USER);
        }
    }
}
