package com.abranlezama.ecommercestore.customer.controller;

import com.abranlezama.ecommercestore.customer.service.CustomerAuthService;
import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerAuthService authenticationService;

    @PostMapping("/register/customer")
    public ResponseEntity<Void> registerCustomer(@Valid @RequestBody RegisterCustomerDTO registerCustomerDto) {
        long userId = authenticationService.register(registerCustomerDto);
        URI uri = URI.create("/customers/" + userId);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/login/customer")
    public String authenticate(@Valid @RequestBody AuthenticationDTO authDto) {
        return authenticationService.authenticate(authDto);
    }
}
