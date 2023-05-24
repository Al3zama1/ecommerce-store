package com.abranlezama.ecommercestore.sharedto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthenticationDTO(
        @Email String email,
        @Size(min = 8, max = 15) String password
) {
}
