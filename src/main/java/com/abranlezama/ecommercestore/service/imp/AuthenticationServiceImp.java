package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.RegisterDTO;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImp implements AuthenticationService {
    @Override
    public long registerCustomer(RegisterDTO registerDto) {
        return 0;
    }
}
