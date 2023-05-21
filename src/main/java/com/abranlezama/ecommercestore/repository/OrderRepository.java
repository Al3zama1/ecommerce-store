package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByCustomer_User_Email(Pageable pageable, String userEmail);
}
