package com.epam.esm.service;

import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAll(int pageNumber, int pageSize);

    Optional<UserResponse> get(int id);

    Optional<UserResponse> get(String login);

    long getCount();

    UserResponse create(CreateUserRequest user);
}
