package com.epam.esm.config;

import com.epam.esm.creator.UserCreator;
import com.epam.esm.properties.GenerationProperties;
import com.epam.esm.properties.UserProperties;
import com.epam.esm.random.primitive.RandomWord;
import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.request.CreateUserRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class UserConfig {
    private CreateUserRequest generateRandomUser(GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        UserProperties userProperties = properties.getUser();
        CreateUserRequest user = new CreateUserRequest();
        user.setFirstName(
                new RandomWord(dictionary,
                        userProperties.getFirstName().getMinSize(),
                        userProperties.getFirstName().getMaxSize()
                ).getValue()
        );
        user.setLastName(
                new RandomWord(dictionary,
                        userProperties.getLastName().getMinSize(),
                        userProperties.getLastName().getMaxSize()
                ).getValue()
        );
        user.setLogin(
                new RandomWord(dictionary,
                        userProperties.getLogin().getMinSize(),
                        userProperties.getLogin().getMaxSize()
                ).getValue()
        );
        user.setPassword(
                new RandomWord(dictionary,
                        userProperties.getPassword().getMinSize(),
                        userProperties.getPassword().getMaxSize()
                ).getValue()
        );
        return user;
    }

    @Bean
    public UserCreator userCreator(UserService userService, GenerationProperties properties, Map<Integer, List<String>> dictionary) {
        return new UserCreator(userService) {
            @Override
            protected CreateUserRequest getUser() {
                return generateRandomUser(properties, dictionary);
            }
        };
    }
}
