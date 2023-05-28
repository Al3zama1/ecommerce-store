package com.abranlezama.ecommercestore.order.repository;

import com.abranlezama.ecommercestore.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
