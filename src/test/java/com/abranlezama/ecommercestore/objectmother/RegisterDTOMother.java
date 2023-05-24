package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;

public class RegisterDTOMother {
    public static RegisterCustomerDTO.RegisterCustomerDTOBuilder customer() {
        return RegisterCustomerDTO.builder()
                .name("duke Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .verifyPassword("12345678");
    }
}
