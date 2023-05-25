package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.UpdateCartItemDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("return HTTP status OK(200) when product is added to customer cart")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void addProductToCustomerCartWhenValidInput() throws Exception {
        // Given
        AddProductToCartDTO addDto = new AddProductToCartDTO(1L, (short) 4);
        String customerEmail = "duke.last@gmail.com";

        // When
        this.mockMvc.perform(post("/api/v1/customers/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDto)))
                .andExpect(status().isOk());

        // Then
        then(customerCartService).should().addProductToCart(addDto, customerEmail);
    }

    @Test
    @DisplayName("return HTTP status OK(200) when updating cart product with valid data")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void returnHttpStatusOKWhenUpdatingCartProductWithValidInput() throws Exception {
        // Given
        UpdateCartItemDTO updateDto = new UpdateCartItemDTO(1L, (short) 5);
        String customerEmail = "duke.last@gmail.com";

        // When
        this.mockMvc.perform(patch("/api/v1/customers/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        // Then
        then(customerCartService).should().updateCartItem(updateDto, customerEmail);
    }

    @Test
    @DisplayName("return HTTP status NO_CONTENT(204) when removing cart item")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void returnHttpStatus204WhenRemovingCustomerCartItem() throws Exception {
        // Given
        long productId = 1L;
        String customerEmail = "duke.last@gmail.com";

        // When
        this.mockMvc.perform(delete("/api/v1/customers/cart/{productId}", productId))
                .andExpect(status().isNoContent());

        // Then
        then(customerCartService).should().removeItemFromCustomerCart(productId, customerEmail);
    }

    @Test
    @DisplayName("return HTTP status UnprocessableEntity(422) when removing cart item with invalid input")
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void returnHttpStatusUnprocessableEntityWhenRemovingCartItemWithInvalidInput() throws Exception {
        // Given
        long productId = -1L;
        String customerEmail = "duke.last@gmail.com";

        // When
        this.mockMvc.perform(delete("/api/v1/customers/cart/{productId}", productId))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(customerCartService).shouldHaveNoInteractions();
    }
}
