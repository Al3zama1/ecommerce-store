package com.abranlezama.ecommercestore.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record AddItemToCartDto(
        @Positive Long productId,
        @Positive Short quantity

) { }
