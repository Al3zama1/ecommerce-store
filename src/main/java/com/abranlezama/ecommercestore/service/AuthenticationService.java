package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.RegisterCustomerDTO;

public interface AuthenticationService {

    void registerCustomer(RegisterCustomerDTO registerCustomerDTO);
}
