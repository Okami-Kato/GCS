package com.epam.esm.dao;

import com.epam.esm.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrderDao extends JpaRepository<UserOrder, Integer> {
    Page<UserOrder> findAllByUserId(int userId, Pageable pageable);

    Page<UserOrder> findAllByCertificateId(int certificateId, Pageable pageable);
}
