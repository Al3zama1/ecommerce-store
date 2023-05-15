package com.abranlezama.ecommercestore.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
public record CartDTO(
        @Valid Set<CartItemDTO> cartItems,
        @PositiveOrZero Float cartTotal
) { }
