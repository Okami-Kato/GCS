package com.epam.esm.service;

import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAll(int pageNumber, int pageSize);

    Optional<UserResponse> get(int id);

    UserResponse create(CreateUserRequest user);
}
