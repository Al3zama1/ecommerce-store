package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.ProductDTO;

public class ProductDTOMother {

    public static ProductDTO.ProductDTOBuilder complete() {
        return ProductDTO.builder()
                .id(1L)
                .name("PlayStation 5")
                .description("Next generation console.")
                .price(500F);
    }
}
