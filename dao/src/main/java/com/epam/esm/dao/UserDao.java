package com.epam.esm.dao;

import com.epam.esm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    String GET_USERS_WITH_HIGHEST_COST =
            "WITH user_cost_sum AS (" +
                    "    SELECT user_id, SUM(cost) cost_sum" +
                    "    FROM user_order" +
                    "    GROUP BY user_id" +
                    ")" +
                    "SELECT u.id, full_name, username, password, enabled " +
                    "FROM user u " +
                    "         INNER JOIN (" +
                    "    SELECT user_id" +
                    "    FROM user_cost_sum" +
                    "    WHERE cost_sum = (SELECT MAX(cost_sum) FROM user_cost_sum)" +
                    ") AS ui ON u.id = ui.user_id";

    @Query(value = GET_USERS_WITH_HIGHEST_COST, nativeQuery = true)
    List<User> findUsersWithTheHighestCost();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
