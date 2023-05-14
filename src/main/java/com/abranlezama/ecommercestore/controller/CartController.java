package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartDTO getCart(Authentication authentication) {
        return cartService.getCustomerCart(authentication.getName());
    }
}
