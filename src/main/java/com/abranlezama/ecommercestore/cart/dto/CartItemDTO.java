package com.abranlezama.ecommercestore.cart.dto;

import lombok.Builder;

@Builder
public record CartItemDTO(
        Long productId,
        String name,
        Float price,
        Short quantity

) { }
