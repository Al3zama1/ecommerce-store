package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.customer.Customer;

public class CustomerMother {

    public static Customer.CustomerBuilder complete() {
        return Customer.builder()
                .id(1L)
                .name("duke Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .enabled(false);
    }
}
