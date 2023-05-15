package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;

public interface CartService {

    CartDTO getCustomerCart(String userEmail);

    void addProductToCart(String userEmail, long productId, int quantity);

    void updateCartProduct(String userEmail, long productId, int quantity);

    void removeCartProduct(String userEmail, long productId);
}
