package com.epam.esm.data_generation.creator;

import com.epam.esm.data_generation.properties.UserOrderProperties;
import com.epam.esm.generator.impl.RandomUserOrder;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserOrderCreator {
    Logger logger = LoggerFactory.getLogger(UserOrderCreator.class);
    private final UserOrderService userOrderService;

    @Autowired
    public UserOrderCreator(UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    public void create(UserOrderProperties properties, List<Integer> availableCertificatesIds, List<Integer> availableUsersIds) {
        long before = userOrderService.getCount();
        for (int i = 0; i < properties.getAmount(); i++) {
            CreateUserOrderRequest order = new RandomUserOrder(availableCertificatesIds, availableUsersIds).generate();
            try {
                userOrderService.create(order);
            } catch (ServiceException e) {
                logger.error(e.getMessage());
            }
        }
        long after = userOrderService.getCount();
        logger.info("Generated {} orders", after - before);
    }
}
