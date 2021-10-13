package com.epam.esm.config;

import com.epam.esm.creator.UserOrderCreator;
import com.epam.esm.random.collection.RandomElementFromCollection;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.UserResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class UserOrderConfig {
    private CreateUserOrderRequest userOrder(Set<Integer> usersIds, Set<Integer> certificatesIds) {
        return new CreateUserOrderRequest(
                new RandomElementFromCollection<>(usersIds).getValue(),
                new RandomElementFromCollection<>(certificatesIds).getValue()
        );
    }

    private Set<Integer> getAvailableUsersIds(UserService userService) {
        return userService.getAll(1, Math.toIntExact(userService.getCount())).stream()
                .map(UserResponse::getId)
                .collect(Collectors.toSet());
    }

    private Set<Integer> getAvailableCertificatesIds(CertificateService certificateService) {
        return certificateService.getAll(1, Math.toIntExact(certificateService.getCount()))
                .stream()
                .map(CertificateItem::getId)
                .collect(Collectors.toSet());
    }

    @Bean
    public UserOrderCreator userOrderCreator(UserOrderService userOrderService, UserService userService,
                                             CertificateService certificateService) {
        Set<Integer> userIds = getAvailableUsersIds(userService);
        Set<Integer> certificateIds = getAvailableCertificatesIds(certificateService);
        return new UserOrderCreator(userOrderService) {
            @Override
            protected CreateUserOrderRequest getUserOrder() {
                return userOrder(userIds, certificateIds);
            }
        };
    }
}
