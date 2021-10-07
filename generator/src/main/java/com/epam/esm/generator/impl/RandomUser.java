package com.epam.esm.generator.impl;

import com.epam.esm.generator.Generator;
import com.epam.esm.service.dto.request.CreateUserRequest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RandomUser implements Generator<CreateUserRequest> {
    private String firstName;
    private String lastName;
    private String login;
    private String password;

    public RandomUser withFirstName(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.firstName = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomUser withLastName(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.lastName = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomUser withLogin(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.login = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    public RandomUser withPassword(int minSize, int maxSize, Map<Integer, List<String>> dictionary) {
        this.password = new RandomSentence(dictionary).withMinSize(minSize).withMaxSize(maxSize).generate();
        return this;
    }

    @Override
    public CreateUserRequest generate() {
        if (firstName == null)
            throw new IllegalStateException("firstName can't be null");
        if (lastName == null)
            throw new IllegalStateException("lastName can't be null");
        if (login == null)
            throw new IllegalStateException("login can't be null");
        if (password == null)
            throw new IllegalStateException("password can't be null");
        return new CreateUserRequest(firstName, lastName, login, password);
    }
}
