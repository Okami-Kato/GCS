package com.epam.esm.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> get(K id);

    List<T> getAll(int pageNumber, int pageSize);

    int getCount();

    void create(T t);

    void update(T t);

    void delete(K id);
}
