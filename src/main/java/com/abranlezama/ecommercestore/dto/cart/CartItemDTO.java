package com.abranlezama.ecommercestore.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CartItemDTO(
        @Positive Long productId,
        @NotBlank String name,
        @NotBlank Float price,
        @Positive Short quantity
) { }
