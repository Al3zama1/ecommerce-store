package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;

public class ProductResponseDTOMother {

    public static ProductResponseDTO.ProductResponseDTOBuilder complete() {
        return ProductResponseDTO.builder()
                .id(1L)
                .name("PlayStation 5")
                .description("Next generation console.")
                .price(500F);
    }
}
