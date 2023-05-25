package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.config.CustomerSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerCartController.class)
@Import(CustomerSecurityConfig.class)
class CustomerCartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerCartService customerCartService;
    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("return HTTP status OK(200) with cart")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void returnHttpStatusOkWithCart() throws Exception {
        // Given
        String customerEmail = "duke.last@gmail.com";

        // When
        this.mockMvc.perform(get("/api/v1/customers/cart"))
                .andExpect(status().isOk());

        // Then
        then(customerCartService).should().retrieveCustomerCart(customerEmail);
    }

}
