package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.event.UserActivationDTO;

import java.io.IOException;

public interface AccountActivationService {

    void sendActivationEmail(UserActivationDTO event) throws IOException;
}
