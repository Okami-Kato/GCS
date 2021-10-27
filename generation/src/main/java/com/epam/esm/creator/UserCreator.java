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
    public int create(int amount) {
        int counter = 0;
        for (int i = 0; i < amount; i++) {
            try {
                CreateUserRequest user;
                do {
                    user = getUser();
                }
                while (userService.findByUsername(user.getUsername()).isPresent());
                userService.create(user);
                counter++;
            } catch (ServiceException e) {
                logger.error(e.getMessage());
            }
        }
        return counter;
    }
}
