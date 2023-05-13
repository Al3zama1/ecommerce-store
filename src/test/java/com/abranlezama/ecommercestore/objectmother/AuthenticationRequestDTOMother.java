package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;

public class AuthenticationRequestDTOMother {

    public static AuthenticationRequestDTO.AuthenticationRequestDTOBuilder complete() {
        return AuthenticationRequestDTO.builder()
                .email("duke.last@gmail.com")
                .password("12345678");
    }
}
