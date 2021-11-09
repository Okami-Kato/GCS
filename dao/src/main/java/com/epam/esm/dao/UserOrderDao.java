package com.epam.esm.dao;

import com.epam.esm.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOrderDao extends JpaRepository<UserOrder, Integer> {
    String GET_USERS_WITH_HIGHEST_COST =
            "WITH user_cost_sum AS (" +
                    "    SELECT user_id, SUM(cost) cost_sum" +
                    "    FROM user_order" +
                    "    GROUP BY user_id" +
                    ")" +
                    "    SELECT user_id" +
                    "    FROM user_cost_sum" +
                    "    WHERE cost_sum = (SELECT MAX(cost_sum) FROM user_cost_sum)";

    @Query(value = GET_USERS_WITH_HIGHEST_COST, nativeQuery = true)
    List<String> findUsersWithTheHighestCost();

    Page<UserOrder> findAllByUserId(String userId, Pageable pageable);

    Page<UserOrder> findAllByCertificateId(int certificateId, Pageable pageable);
}
