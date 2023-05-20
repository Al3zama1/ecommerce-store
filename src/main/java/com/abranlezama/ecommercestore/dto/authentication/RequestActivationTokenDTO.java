package com.abranlezama.ecommercestore.dto.authentication;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record RequestActivationTokenDTO(
        @Email String userEmail
) { }
