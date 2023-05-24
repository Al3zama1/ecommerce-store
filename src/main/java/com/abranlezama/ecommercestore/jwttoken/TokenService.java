package com.abranlezama.ecommercestore.jwttoken;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateJwt(Authentication authentication);
}
