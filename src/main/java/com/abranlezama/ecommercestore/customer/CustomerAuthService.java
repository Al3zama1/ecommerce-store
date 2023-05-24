package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;

public interface CustomerAuthService {

    long registerCustomer(RegisterCustomerDTO registerCustomerDto);
}
