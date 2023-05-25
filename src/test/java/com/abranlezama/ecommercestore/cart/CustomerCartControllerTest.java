package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.config.CustomerSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerCartController.class)
@Import(CustomerSecurityConfig.class)
@DisplayName("customer cart")
class CustomerCartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CustomerCartService customerCartService;
    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;


    @Nested
    @DisplayName("block non customers from accessing cart")
    class BlockNonCustomersFromCart {

        @Test
        @DisplayName("return HTTP status 401 when not authenticated")
        void return401WhenNotNotAuthenticated() throws Exception {
            // Given

            // When
            mockMvc.perform(get("/api/v1/customers/cart"))
                    .andExpect(status().isUnauthorized());

            // Then
            then(customerCartService).shouldHaveNoInteractions();
        }
        @Test
        @DisplayName("return HTTP status 403 when not a customer")
        @WithMockUser(username = "duke.last@gmail.com")
        void return403WhenNotAuthorizedToAccessCart() throws Exception {
            // Given

            // When
            mockMvc.perform(get("/api/v1/customers/cart"))
                    .andExpect(status().isForbidden());

            // Then
            then(customerCartService).shouldHaveNoInteractions();
        }
    }

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

    @Test
    @DisplayName("add product to customer cart")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void addProductToCustomerCartWhenValidInput() throws Exception {
        // Given
        AddProductToCartDTO addDto = new AddProductToCartDTO(1L, (short) 4);

        // When
        this.mockMvc.perform(post("/api/v1/customers/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDto)))
                .andExpect(status().isOk());

        // Then
        then(customerCartService).should().addProductToCart(addDto);
    }
}
