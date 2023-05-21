package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.product.AddProductDTO;
import com.abranlezama.ecommercestore.dto.product.ProductDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;

import java.util.List;
import java.util.Set;

public interface ProductService {

    List<ProductDTO> getProducts(int page, int pageSize, Set<String> categories);

    Long createProduct(String userEmail, AddProductDTO requestDto);

    void removeProduct(String userEmail, Long productId);

    ProductDTO updateProduct(String userEmail, long productId, UpdateProductRequestDTO requestDto);
}
