package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.order.OrderResponseDTO;
import com.abranlezama.ecommercestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Set<OrderResponseDTO> getOrders(Authentication authentication) {
        return orderService.getCustomerOrders(authentication.getName());
    }

}
