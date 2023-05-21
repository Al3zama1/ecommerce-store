package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationDTO;

public class AuthenticationDTOMother {

    public static AuthenticationDTO.AuthenticationDTOBuilder complete() {
        return AuthenticationDTO.builder()
                .email("duke.last@gmail.com")
                .password("12345678");
    }
}
