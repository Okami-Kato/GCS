package com.epam.esm.generator.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.generator.Generator;

import java.util.List;

public class RandomUserOrder implements Generator<UserOrder> {
    private final List<Certificate> availableCertificates;
    private final List<User> availableUsers;

    public RandomUserOrder(List<Certificate> availableCertificates, List<User> availableUsers) {
        this.availableCertificates = availableCertificates;
        this.availableUsers = availableUsers;
    }

    @Override
    public UserOrder generate() {
        Certificate certificate = availableCertificates.get(new RandomInteger().min(0).max(availableCertificates.size()).generate());
        User user = availableUsers.get(new RandomInteger().min(0).max(availableUsers.size()).generate());
        return new UserOrder(user, certificate, certificate.getPrice());
    }
}
