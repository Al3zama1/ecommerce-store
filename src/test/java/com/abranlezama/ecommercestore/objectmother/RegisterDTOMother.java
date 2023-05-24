package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.authentication.RegisterDTO;

public class RegisterDTOMother {
    public static RegisterDTO.RegisterDTOBuilder customer() {
        return RegisterDTO.builder()
                .name("duke Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .verifyPassword("12345678");
    }
}
