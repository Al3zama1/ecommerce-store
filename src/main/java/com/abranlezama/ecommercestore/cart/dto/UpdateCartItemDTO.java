package com.abranlezama.ecommercestore.cart.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemDTO(
        @Positive Long productId,
        @Positive Short quantity
) {
}
