package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponseDTO> getProducts(
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @Positive @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "categories", defaultValue = "") List<String> categories) {
        return productService.getProducts(page, pageSize, categories);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Void> createProduct(Authentication authentication,
                                              @Valid @RequestBody AddProductRequestDTO requestDto) {
        long productId = productService.createProduct(authentication.getName(), requestDto);
        return ResponseEntity.created(URI.create("/products/" + productId)).build();
    }
}
