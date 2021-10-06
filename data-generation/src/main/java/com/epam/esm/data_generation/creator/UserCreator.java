package com.epam.esm.data_generation.creator;

import com.epam.esm.data_generation.properties.UserProperties;
import com.epam.esm.generator.impl.RandomUser;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import com.epam.esm.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserCreator {
    Logger logger = LoggerFactory.getLogger(UserCreator.class);
    private final UserService userService;
    private final Map<Integer, List<String>> dictionary;

    @Autowired
    public UserCreator(UserService userService, Map<Integer, List<String>> dictionary) {
        this.userService = userService;
        this.dictionary = dictionary;
    }

    public void create(UserProperties properties) {
        long before = userService.getCount();
        for (int i = 0; i < properties.getAmount(); i++) {
            CreateUserRequest user = new RandomUser()
                    .withFirstName(properties.getFirstName().getMinSize(), properties.getFirstName().getMaxSize(), dictionary)
                    .withLastName(properties.getLastName().getMinSize(), properties.getLastName().getMaxSize(), dictionary)
                    .withLogin(properties.getLogin().getMinSize(), properties.getLogin().getMaxSize(), dictionary)
                    .withPassword(properties.getPassword().getMinSize(), properties.getPassword().getMaxSize(), dictionary)
                    .generate();
            try {
                userService.create(user);
            } catch (ServiceException e) {
                logger.error(e.getMessage());
                i--;
            }
        }
        long after = userService.getCount();
        logger.info("Generated {} users", after - before);
    }
}
