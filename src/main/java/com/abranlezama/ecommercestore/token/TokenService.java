package com.abranlezama.ecommercestore.token;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateJwt(Authentication authentication);
}
