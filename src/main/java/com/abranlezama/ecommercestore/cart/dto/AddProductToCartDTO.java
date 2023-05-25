package com.abranlezama.ecommercestore.cart.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record AddProductToCartDTO(
        @Positive Long productId,
        @Positive Short quantity
) {
}
