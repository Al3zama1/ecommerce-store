package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.CustomerRegisterDTO;

public interface CustomerAuthService {

    long registerCustomer(CustomerRegisterDTO customerRegisterDto);
}
