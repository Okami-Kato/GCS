package com.epam.esm.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> find(K id);

    List<T> findAll(int pageNumber, int pageSize);

    long getCount();

    void create(T t);

    T update(T t);

    void delete(K id);
}
