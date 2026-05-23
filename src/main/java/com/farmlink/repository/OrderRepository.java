package com.farmlink.repository;

import com.farmlink.model.Order;
import com.farmlink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);
    List<Order> findByFarmerOrderByCreatedAtDesc(User farmer);
    List<Order> findByStatus(Order.OrderStatus status);
}
