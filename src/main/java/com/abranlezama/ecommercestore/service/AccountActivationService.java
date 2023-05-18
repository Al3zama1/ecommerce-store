package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.event.UserActivationDetails;

import java.io.IOException;

public interface AccountActivationService {

    void sendActivationEmail(UserActivationDetails event) throws IOException;
}
