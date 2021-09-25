package com.epam.esm.dao;

import com.epam.esm.entity.UserOrder;

import java.util.List;

public interface UserOrderDao extends Dao<UserOrder, Integer> {
    List<UserOrder> getAll(int pageNumber, int pageSize, int userId);
}
