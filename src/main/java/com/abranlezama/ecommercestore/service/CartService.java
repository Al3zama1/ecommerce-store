package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;

public interface CartService {

    CartDTO getCustomerCart(String userEmail);
}
