package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.service.AuthenticationService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({SecurityConfiguration.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtAuthenticationConverter authenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCallProductServiceToFetchProducts() throws Exception{
        // Given
        String page = "0";
        String pageSize = "20";

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                .param("page", page)
                .param("pageSize", pageSize))
                .andExpect(status().isOk());

    }

}
