package com.epam.esm.service;

import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);

    Optional<UserResponse> findById(int id);

    Optional<UserResponse> findByLogin(String login);

    List<UserResponse> findUsersWithTheHighestCost();

    long getCount();

    UserResponse create(CreateUserRequest user);
}
