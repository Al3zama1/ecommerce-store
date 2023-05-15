package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer_User_Email(String userEmail);
}
