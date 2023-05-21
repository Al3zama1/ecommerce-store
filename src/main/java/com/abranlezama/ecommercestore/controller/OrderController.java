package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.service.OrderService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Set<OrderDTO> getOrders(Authentication authentication,
                                   @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @Positive @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return orderService.getCustomerOrders(authentication.getName(), page, pageSize);
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(Authentication authentication) {
        long orderId = orderService.createOrder(authentication.getName());;
        URI uri = URI.create("/orders/" + orderId);
        return ResponseEntity.created(uri).build();
    }

}
