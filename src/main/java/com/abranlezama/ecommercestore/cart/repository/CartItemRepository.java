package com.abranlezama.ecommercestore.cart.repository;

import com.abranlezama.ecommercestore.cart.model.CartItem;
import com.abranlezama.ecommercestore.cart.model.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
}
