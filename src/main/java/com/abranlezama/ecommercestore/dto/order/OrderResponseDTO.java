package com.abranlezama.ecommercestore.dto.order;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderResponseDTO(
        Long id,
        Float totalCost,
        String orderStatus,
        LocalDateTime datePlaced,
        LocalDateTime dateShipped,
        LocalDateTime dateDelivered
) {
}
