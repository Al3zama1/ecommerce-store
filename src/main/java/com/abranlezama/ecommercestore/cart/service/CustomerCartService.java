package com.abranlezama.ecommercestore.cart.service;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.cart.dto.UpdateCartItemDTO;

public interface CustomerCartService {

    CartDTO retrieveCustomerCart(String customerEmail);
    void addProductToCart(AddProductToCartDTO addTdo, String customerEmail);

    void updateCartItem(UpdateCartItemDTO updateDto, String customerEmail);

    void removeItemFromCustomerCart(long productId, String customerEmail);
}
