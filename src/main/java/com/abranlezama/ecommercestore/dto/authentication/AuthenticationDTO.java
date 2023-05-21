package com.abranlezama.ecommercestore.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record AuthenticationDTO(
        @Email String email,
        @NotNull @Size(min = 8, max = 15) String password
) {
}
