package com.abranlezama.ecommercestore.order.controller;

import com.abranlezama.ecommercestore.order.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/customers/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<Void> createCustomerOrder(Authentication authentication,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        long orderId = customerOrderService.createCustomerOrder(authentication.getName());
        URI location = uriComponentsBuilder.path("/customers/orders/{orderId}").build(orderId);

        return ResponseEntity.created(location).build();
    }
}
