package com.abranlezama.ecommercestore.product;

import com.abranlezama.ecommercestore.product.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> retrieveProduct(int page, int pageSize);
}
