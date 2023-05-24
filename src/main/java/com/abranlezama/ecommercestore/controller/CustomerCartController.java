package com.abranlezama.ecommercestore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerCartController {

    @GetMapping
    public String cart() {
        return "You are in the customer cart";
    }
}
