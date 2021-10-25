package com.epam.esm.service;

import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.service.dto.response.UserOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserOrderService {
    Page<UserOrderItem> findAll(Pageable pageable);

    Page<UserOrderItem> findAllByUserId(int userId, Pageable pageable);

    Page<UserOrderItem> findAllByCertificateId(int certificateId, Pageable pageable);

    Optional<UserOrderResponse> findById(int id);

    long getCount();

    UserOrderResponse create(CreateUserOrderRequest userOrder);
}
