package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.CustomerRegisterDTO;
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
public class AuthenticationController {

    private final CustomerAuthService authenticationService;

    @PostMapping("/register/customer")
    public ResponseEntity<Void> registerCustomer(@Valid @RequestBody CustomerRegisterDTO customerRegisterDto) {
        long userId = authenticationService.registerCustomer(customerRegisterDto);
        URI uri = URI.create("/customers/" + userId);
        return ResponseEntity.created(uri).build();
    }
}
