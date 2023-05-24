package com.abranlezama.ecommercestore.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer_Email(String customerEmail);
}
