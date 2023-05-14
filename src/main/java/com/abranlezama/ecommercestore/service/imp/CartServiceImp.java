package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImp implements CartService {
    @Override
    public CartDTO getCustomerCart(String userEmail) {
        return null;
    }
}
