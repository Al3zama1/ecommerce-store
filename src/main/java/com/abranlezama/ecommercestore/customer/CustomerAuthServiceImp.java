package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.CustomerRegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImp implements CustomerAuthService {

    private final AuthenticationManager authenticationManager;
    @Override
    public long registerCustomer(CustomerRegisterDTO customerRegisterDto) {
        return 0;
    }
}
