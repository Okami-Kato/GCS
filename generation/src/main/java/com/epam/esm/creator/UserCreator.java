package com.epam.esm.creator;

import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserCreator implements Creator {
    Logger logger = LoggerFactory.getLogger(UserCreator.class);
    private final UserService userService;

    public UserCreator(UserService userService) {
        this.userService = userService;
    }

    protected abstract CreateUserRequest getUser();

    @Override
    public void create(int amount) {
        for (int i = 0; i < amount; i++) {
            try {
                userService.create(getUser());
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                i--;
            }
        }
    }
}
