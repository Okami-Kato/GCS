package com.epam.esm.dao;

import com.epam.esm.entity.User;

import java.util.Optional;

public interface UserDao extends Dao<User, Integer> {
    Optional<User> get(String login);
}
