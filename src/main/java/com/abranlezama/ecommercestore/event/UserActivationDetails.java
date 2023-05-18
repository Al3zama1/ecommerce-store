package com.abranlezama.ecommercestore.event;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
public record UserActivationDetails(
        @Email String userEmail,
        @NotBlank String name,
        @NotBlank String token
) { }
