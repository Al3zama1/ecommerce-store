package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfiguration.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtAuthenticationConverter authenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    // Customer registration
    @Test
    void shouldCallAuthenticationServiceToRegisterCustomer() throws Exception {
        // Given
        RegisterCustomerDTO request = RegisterCustomerDTOMother.complete().build();

        // When
        mockMvc.perform(post("/auth/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.is("/auth/login")));

        // Then
        then(authenticationService).should().registerCustomer(request);
    }

    @Test
    void shouldThrow422WhenUserProvidesMalformedInput() throws Exception {
        // Given
        RegisterCustomerDTO request = RegisterCustomerDTOMother.complete()
                .phoneNumber("323480-3333")
                .build();

        // When
        mockMvc.perform(post("/auth/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors[0].message",
                        Matchers.containsString("Phone number must be in format XXX-XXX-XXXX")));

        // Then
        then(authenticationService).should(never()).registerCustomer(any());
    }

    // User authentication
    @Test
    void shouldCallAuthenticationServiceToAuthenticateUser() throws Exception {
        // Given
        AuthenticationRequestDTO dto = AuthenticationRequestDTO.builder()
                .email("duke.last@gmail.com")
                .password("12345678")
                .build();

        given(authenticationService.authenticateUser(dto)).willReturn("token");

        // When
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is("token")));

        // Then
        then(authenticationService).should().authenticateUser(dto);
    }


}
