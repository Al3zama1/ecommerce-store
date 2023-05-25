package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CustomerCartService customerCartService;

    @GetMapping
    public CartDTO retrieveCustomerCart(Authentication authentication) {
        return customerCartService.retrieveCustomerCart(authentication.getName());
    }
}
