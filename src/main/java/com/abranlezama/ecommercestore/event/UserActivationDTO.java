package com.abranlezama.ecommercestore.event;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record UserActivationDTO(
        @Email String userEmail,
        @NotBlank String name,
        @NotBlank String token
) { }
