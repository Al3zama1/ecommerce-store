package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getProducts(int page, int pageSize, List<String> categories);

    Long createProduct(String userEmail, AddProductRequestDTO requestDto);
}
