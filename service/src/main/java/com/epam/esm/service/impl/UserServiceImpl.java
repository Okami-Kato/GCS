package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<UserResponse> getAll(int pageNumber, int pageSize) {
        try {
            return userDao.getAll(pageNumber, pageSize).stream()
                    .map(user -> mapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<UserResponse> get(int id) {
        return userDao.get(id).map(user -> mapper.map(user, UserResponse.class));
    }

    @Override
    public long getCount() {
        return userDao.getCount();
    }

    @Override
    public UserResponse create(CreateUserRequest user) {
        User userToCreate = mapper.map(user, User.class);
        try {
            userDao.create(userToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(e);
        }
        return mapper.map(userToCreate, UserResponse.class);
    }
}
