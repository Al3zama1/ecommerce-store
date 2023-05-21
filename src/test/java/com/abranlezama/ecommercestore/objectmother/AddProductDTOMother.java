package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.AddProductDTO;

public class AddProductDTOMother {

    public static AddProductDTO.AddProductDTOBuilder create() {
        return AddProductDTO.builder()
                .name("PlayStation 5")
                .description("Next generation console.")
                .price(500F)
                .stockQuantity(300);
    }
}
