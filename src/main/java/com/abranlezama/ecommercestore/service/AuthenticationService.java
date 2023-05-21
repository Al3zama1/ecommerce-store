package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.RequestActivationTokenDTO;

public interface AuthenticationService {

    void registerCustomer(RegisterCustomerDTO registerCustomerDTO);

    String authenticateUser(AuthenticationDTO dto);

    void activateUserAccount(String token);

    String resendAccountActivationToken(RequestActivationTokenDTO requestDto);
}
