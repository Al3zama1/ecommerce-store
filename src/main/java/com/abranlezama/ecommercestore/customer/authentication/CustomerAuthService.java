package com.abranlezama.ecommercestore.customer.authentication;

import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;

public interface CustomerAuthService {

    long register(RegisterCustomerDTO registerCustomerDto);

    String authenticate(AuthenticationDTO authDto);
}
