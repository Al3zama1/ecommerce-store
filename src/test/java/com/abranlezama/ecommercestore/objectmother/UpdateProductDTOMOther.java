package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.product.UpdateProductDTO;

import java.util.Set;

public class UpdateProductDTOMOther {

    public static UpdateProductDTO.UpdateProductDTOBuilder complete() {
        return UpdateProductDTO.builder()
                .name("Soccer Ball")
                .description("Next generation soccer ball")
                .price(40F)
                .stockQuantity(50)
                .categories(Set.of("sports"));
    }
}
