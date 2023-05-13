package com.abranlezama.ecommercestore.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public void getProducts(@PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
                            @Positive @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

    }
}
