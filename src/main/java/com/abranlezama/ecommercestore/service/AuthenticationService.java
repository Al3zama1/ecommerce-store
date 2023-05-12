package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;

public interface AuthenticationService {

    void registerCustomer(RegisterCustomerDTO registerCustomerDTO);

    String authenticateUser(AuthenticationRequestDTO dto);
}
