package com.epam.esm.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> get(K id);

    List<T> getAll();

    T create(T t);

    boolean update(T t);

    boolean delete(K id);

    boolean idExists(K id);
}
