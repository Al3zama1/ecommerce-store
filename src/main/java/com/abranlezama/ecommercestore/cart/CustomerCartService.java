package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;

public interface CustomerCartService {

    CartDTO retrieveCustomerCart(String customerEmail);
    void addProductToCart(AddProductToCartDTO addTdo, String customerEmail);
}
