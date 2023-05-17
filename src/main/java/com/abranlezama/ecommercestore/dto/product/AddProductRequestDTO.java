package com.abranlezama.ecommercestore.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.Set;

@Builder
public record AddProductRequestDTO(
        @NotBlank String name,
        String description,
        @Positive Float price,
        @PositiveOrZero Integer stockQuantity,
        Set<String> categories

) {
}
