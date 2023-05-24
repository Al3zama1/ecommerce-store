package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.CustomerSecurityConfig;
import com.abranlezama.ecommercestore.dto.authentication.RegisterDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterDTOMother;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.imp.JpaCustomerDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebMvcTest(AuthenticationController.class)
@Import(CustomerSecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private JpaCustomerDetailsService jpaCustomerDetailsService;
//    @MockBean
//    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCallCustomerRegisterEndpoint() throws Exception {
        // Given
        RegisterDTO registerDto = RegisterDTOMother.customer().build();
        long userId = 1L;

        given(authenticationService.registerCustomer(registerDto)).willReturn(1L);

        // When
//        this.mockMvc.perform(post("/api/v1/customers")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", is("/customers/" + 1L)));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Then
        then(authenticationService).should().registerCustomer(registerDto);
    }

}
