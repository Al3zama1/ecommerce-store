package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;

import java.util.List;
import java.util.Set;

public interface ProductService {

    List<ProductResponseDTO> getProducts(int page, int pageSize, Set<String> categories);

    Long createProduct(String userEmail, AddProductRequestDTO requestDto);

    void removeProduct(String userEmail, Long productId);

    ProductResponseDTO updateProduct(String userEmail, long productId, UpdateProductRequestDTO requestDto);
}
