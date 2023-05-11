package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/customer")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterCustomerDTO registerCustomerDTO) {
        authenticationService.registerCustomer(registerCustomerDTO);
        return ResponseEntity.created(URI.create("/auth/login")).build();
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody RegisterCustomerDTO registerCustomerDTO) {
//        return authenticationService.loginUser(registerDTO.email(), registerDTO.password());
        return null;
    }
}
