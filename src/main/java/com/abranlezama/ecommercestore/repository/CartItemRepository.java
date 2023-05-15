package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.CartItem;
import com.abranlezama.ecommercestore.model.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
}
