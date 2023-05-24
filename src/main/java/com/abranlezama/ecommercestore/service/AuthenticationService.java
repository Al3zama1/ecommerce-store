package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.authentication.RegisterDTO;

public interface AuthenticationService {

    long registerCustomer(RegisterDTO registerDto);
}
