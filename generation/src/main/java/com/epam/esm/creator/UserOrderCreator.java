package com.epam.esm.creator;

import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserOrderCreator implements Creator{
    Logger logger = LoggerFactory.getLogger(UserOrderCreator.class);
    private final UserOrderService userOrderService;

    public UserOrderCreator(UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    protected abstract CreateUserOrderRequest getUserOrder();

    @Override
    public int create(int amount) {
        int counter = 0;
        for (int i = 0; i < amount; i++) {
            try {
                userOrderService.create(getUserOrder());
                counter++;
            } catch (ServiceException e) {
                logger.error(e.getMessage());
            }
        }
        return counter;
    }
}
