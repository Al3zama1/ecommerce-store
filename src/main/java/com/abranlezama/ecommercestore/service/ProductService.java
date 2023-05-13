package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getProducts(int page, int pageSize);
}
