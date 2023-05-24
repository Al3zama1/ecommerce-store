package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.customer.dto.authentication.CustomerRegisterDTO;

public class RegisterDTOMother {
    public static CustomerRegisterDTO.CustomerRegisterDTOBuilder customer() {
        return CustomerRegisterDTO.builder()
                .name("duke Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .verifyPassword("12345678");
    }
}
