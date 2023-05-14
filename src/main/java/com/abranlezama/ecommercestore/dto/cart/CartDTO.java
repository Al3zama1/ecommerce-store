package com.abranlezama.ecommercestore.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.Set;

@Builder
public record CartDTO(
        @Valid Set<CartItemDTO> cartItems,
        @PositiveOrZero Float cartTotal
) {
}
