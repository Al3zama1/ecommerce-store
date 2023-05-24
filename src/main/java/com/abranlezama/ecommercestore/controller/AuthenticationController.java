package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.authentication.RegisterDTO;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerCustomer(@Valid @RequestBody RegisterDTO registerDto) {
        long userId = authenticationService.registerCustomer(registerDto);
        URI uri = URI.create("/customers/" + userId);
        return ResponseEntity.created(uri).build();
    }
}
