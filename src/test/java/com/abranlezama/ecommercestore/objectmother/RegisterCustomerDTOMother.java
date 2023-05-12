package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;

public class RegisterCustomerDTOMother {

    public static RegisterCustomerDTO.RegisterCustomerDTOBuilder complete() {
        return RegisterCustomerDTO.builder()
                .firstName("Duke")
                .lastName("Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .verifyPassword("12345678")
                .phoneNumber("323-889-3333")
                .street("7788 S 55ST")
                .city("Los Angeles")
                .state("California")
                .postalCode("90005");
    }
}
