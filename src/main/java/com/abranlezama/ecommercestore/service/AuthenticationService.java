package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.RequestActivationTokenDTO;

public interface AuthenticationService {

    void registerCustomer(RegisterCustomerDTO registerCustomerDTO);

    String authenticateUser(AuthenticationRequestDTO dto);

    void activateUserAccount(String token);

    String resendAccountActivationToken(RequestActivationTokenDTO requestDto);
}
