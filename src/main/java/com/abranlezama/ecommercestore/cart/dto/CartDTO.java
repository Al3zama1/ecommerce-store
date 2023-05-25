package com.abranlezama.ecommercestore.cart.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.List;

@Builder
public record CartDTO(
    @PositiveOrZero Float totalCost,
    List<CartItemDTO> cartItems
) { }
