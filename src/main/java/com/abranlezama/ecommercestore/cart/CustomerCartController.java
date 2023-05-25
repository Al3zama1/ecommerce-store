package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CustomerCartService customerCartService;

    @GetMapping
    public CartDTO retrieveCustomerCart(Authentication authentication) {
        return this.customerCartService.retrieveCustomerCart(authentication.getName());
    }

    @PostMapping
    public void addProductToCustomerCart(Authentication authentication,
                                         @Valid @RequestBody AddProductToCartDTO addDto) {
        this.customerCartService.addProductToCart(addDto, authentication.getName());
    }
}
