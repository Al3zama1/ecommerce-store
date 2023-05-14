package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.ProductService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfiguration.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtAuthenticationConverter authenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCallProductServiceToFetchProducts() throws Exception{
        // Given
        int page = 0;
        int pageSize = 20;

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize))
                .param("categories", ""))
                .andExpect(status().isOk());

        // Then
        then(productService).should().getProducts(page, pageSize, List.of());
    }

    @Test
    void shouldReturn422WhenInvalidPageParametersAreProvided() throws Exception{
        // Given
        int page = -1;
        int pageSize = 20;

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("categories", ""))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

}
