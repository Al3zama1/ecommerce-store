package com.abranlezama.ecommercestore.order.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderDTO(
        Long id,
        Float totalCost,
        String status,
        LocalDateTime datePlaced,
        LocalDateTime dateShipped,
        LocalDateTime dateDelivered
) {
}
