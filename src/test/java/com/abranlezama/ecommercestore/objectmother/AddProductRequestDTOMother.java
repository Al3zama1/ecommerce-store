package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;

public class AddProductRequestDTOMother {

    public static AddProductRequestDTO.AddProductRequestDTOBuilder create() {
        return AddProductRequestDTO.builder()
                .name("PlayStation 5")
                .description("Next generation console.")
                .price(500F)
                .stockQuantity(300);
    }
}
