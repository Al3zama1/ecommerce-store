package com.abranlezama.ecommercestore.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record ProductDTO(
    Long id,
    @NotBlank String name,
    @NotBlank String description,
    @PositiveOrZero Float price
) { }
