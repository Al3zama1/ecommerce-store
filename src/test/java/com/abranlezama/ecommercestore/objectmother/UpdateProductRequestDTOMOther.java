package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;

import java.util.Set;

public class UpdateProductRequestDTOMOther {

    public static UpdateProductRequestDTO.UpdateProductRequestDTOBuilder complete() {
        return UpdateProductRequestDTO.builder()
                .name("Soccer Ball")
                .description("Next generation soccer ball")
                .price(40F)
                .stockQuantity(50)
                .categories(Set.of("sports"));
    }
}
