package com.abranlezama.ecommercestore.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ProductDTO(
    @Positive Long id,
    @NotBlank String name,
    String description,
    @Positive Float price
) { }
