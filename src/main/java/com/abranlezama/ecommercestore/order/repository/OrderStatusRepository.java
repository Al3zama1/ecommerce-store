package com.abranlezama.ecommercestore.order.repository;

import com.abranlezama.ecommercestore.order.model.OrderStatus;
import com.abranlezama.ecommercestore.order.util.OrderStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Short> {

    Optional<OrderStatus> findByStatus(OrderStatusType orderStatusType);
}
