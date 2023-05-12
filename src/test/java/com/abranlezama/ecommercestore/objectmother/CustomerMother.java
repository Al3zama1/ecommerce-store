package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.model.Customer;

public class CustomerMother {

    public static Customer.CustomerBuilder complete() {
        return Customer.builder()
                .firstName("Duke")
                .lastName("Last")
                .phoneNumber("323889-3333")
                .street("7788 S 55ST")
                .city("Los Angeles")
                .state("California")
                .postalCode("90005");
    }

}
