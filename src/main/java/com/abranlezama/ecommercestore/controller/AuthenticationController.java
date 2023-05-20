package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/customer")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterCustomerDTO registerCustomerDTO) {
        authenticationService.registerCustomer(registerCustomerDTO);
        return ResponseEntity.created(URI.create("/auth/login")).build();
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody AuthenticationRequestDTO requestDTO) {
        return authenticationService.authenticateUser(requestDTO);
    }

    @GetMapping("/activate-account")
    public void activateUserAccount(@Size(min = 36, max = 36) @RequestParam("token") String token) {
        this.authenticationService.activateUserAccount(token);
    }
}
