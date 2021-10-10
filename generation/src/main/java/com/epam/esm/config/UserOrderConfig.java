package com.epam.esm.config;

import com.epam.esm.creator.UserOrderCreator;
import com.epam.esm.random.collection.RandomElementFromCollection;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class UserOrderConfig {
    @Bean
    @Scope("prototype")
    public CreateUserOrderRequest userOrder(@Qualifier("userIds") Set<Integer> usersIds,
                                            @Qualifier("certificateIds") Set<Integer> certificatesIds) {
        return new CreateUserOrderRequest(
                new RandomElementFromCollection<>(usersIds).getValue(),
                new RandomElementFromCollection<>(certificatesIds).getValue()
        );
    }

    @Bean
    public UserOrderCreator userOrderCreator(UserOrderService userOrderService,
                                             @Qualifier("certificateIds") Set<Integer> certificateIds,
                                             @Qualifier("userIds") Set<Integer> userIds) {
        return new UserOrderCreator(userOrderService) {
            @Override
            protected CreateUserOrderRequest getUserOrder() {
                return userOrder(userIds, certificateIds);
            }
        };
    }

    @Bean
    @Scope("prototype")
    @Qualifier("userIds")
    public Set<Integer> availableUsersIds(UserService userService) {
        return userService.getAll(1, Math.toIntExact(userService.getCount())).stream()
                .map(UserResponse::getId)
                .collect(Collectors.toSet());
    }

    @Bean
    @Scope("prototype")
    @Qualifier("certificateIds")
    public Set<Integer> availableCertificatesIds(CertificateService certificateService) {
        return certificateService.getAll(1, Math.toIntExact(certificateService.getCount()))
                .stream()
                .map(CertificateItem::getId)
                .collect(Collectors.toSet());
    }
}
