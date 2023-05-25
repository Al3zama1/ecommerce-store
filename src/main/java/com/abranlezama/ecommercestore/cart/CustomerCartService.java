package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.CartDTO;

public interface CustomerCartService {

    CartDTO retrieveCustomerCart(String customerEmail);
}
