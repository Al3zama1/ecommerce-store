package com.abranlezama.ecommercestore.customer.service;

import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;

public interface CustomerAuthService {

    long register(RegisterCustomerDTO registerCustomerDto);

    String authenticate(AuthenticationDTO authDto);
}
