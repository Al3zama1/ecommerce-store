package com.abranlezama.ecommercestore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @GetMapping("/products")
    public String retrieveProducts() {
        return "You are in the products page";
    }

}
