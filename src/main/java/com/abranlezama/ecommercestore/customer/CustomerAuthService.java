package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;

public interface CustomerAuthService {

    long register(RegisterCustomerDTO registerCustomerDto);

    String authenticate(AuthenticationDTO authDto);
}
