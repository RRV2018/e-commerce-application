package com.omsoft.retail.order.repo;

import com.omsoft.retail.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(String userId);
}
