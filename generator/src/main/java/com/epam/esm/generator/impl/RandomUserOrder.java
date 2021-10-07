package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;

import java.util.List;

public class RandomUserOrder implements Generator<CreateUserOrderRequest> {
    private final List<Integer> availableCertificatesIds;
    private final List<Integer> availableUsersIds;

    public RandomUserOrder(List<Integer> availableCertificatesIds, List<Integer> availableUsersIds) {
        this.availableCertificatesIds = availableCertificatesIds;
        this.availableUsersIds = availableUsersIds;
    }

    @Override
    public CreateUserOrderRequest generate() {
        Integer certificateId = availableCertificatesIds.get(new RandomInteger().min(0).max(availableCertificatesIds.size() - 1).generate());
        Integer userId = availableUsersIds.get(new RandomInteger().min(0).max(availableUsersIds.size() - 1).generate());
        return new CreateUserOrderRequest(userId, certificateId);
    }
}
