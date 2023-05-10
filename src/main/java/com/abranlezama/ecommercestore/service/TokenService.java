package com.abranlezama.ecommercestore.service;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateJwt(Authentication authentication);
}
