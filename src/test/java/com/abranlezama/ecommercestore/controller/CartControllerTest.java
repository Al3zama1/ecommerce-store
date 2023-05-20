package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.dto.cart.AddItemToCartDto;
import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Set;

import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@Import(value = {SecurityConfiguration.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private CartService cartService;
    @MockBean
    private JwtDecoder jwtDecoder;


    // Retrieve customer cart
    @Test
    @WithMockUser(roles = "CUSTOMER", username = "duke.last@gmail.com")
    void shouldCallCartServiceToRetrieveCustomerItems() throws Exception {
        // Given
        User user = UserMother.complete()
                .roles(Set.of(Role.builder()
                        .role(RoleType.CUSTOMER).build()))
                .build();


        // When
        this.mockMvc.perform(get("/cart"))
                .andExpect(status().isOk());

        // Then
        then(cartService).should().getCustomerCart(user.getEmail());
    }

    @Test
    void shouldThrow401WhenUnauthenticatedUserRequestsCart() throws Exception {
        // Given

        // When
        this.mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrow403WhenUserDoesNotHaveCustomerRole() throws Exception {
        // Given
        User user = UserMother.complete()
                .roles(Set.of())
                .build();

        // When
        this.mockMvc.perform(get("/cart")
                        .with(user(user)))
                .andExpect(status().isForbidden());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    // Add item to shopping cart
    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldAddProductToCustomerCart() throws Exception {
        // Given
        AddItemToCartDto dto = AddItemToCartDto.builder()
                .productId(1L)
                .quantity(2)
                .build();

        // When
        this.mockMvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Then
        then(cartService).should()
                .addProductToCart("duke.last@gmail.com", dto.productId(), dto.quantity());
    }

    // Update cart item tests

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldUpdateCartProductQuantity() throws Exception {
        // Given
        long productId = 1L;
        int quantity = 3;

        // When
        this.mockMvc.perform(patch("/cart")
                .param("productId", String.valueOf(productId))
                .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().updateCartProduct("duke.last@gmail.com", productId, quantity);
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldReturn422WhenParametersAreInvalid() throws Exception {
        // Given
        long productId = -1L;
        int quantity = 0;

        // When
        this.mockMvc.perform(patch("/cart")
                        .param("productId", String.valueOf(productId))
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldReturn400WhenRequestToUpdateCartIsMadeWithMissingParameters() throws Exception {
        // Given
        long productId = -1L;
        int quantity = 0;

        // When
        this.mockMvc.perform(patch("/cart"))
                .andExpect(status().isBadRequest());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    // remove product from customer's cart

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldRemoveProductFromCustomerCart() throws Exception {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;

        // When
        this.mockMvc.perform(delete("/cart")
                .param("productId", String.valueOf(productId)))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().removeCartProduct(userEmail, productId);
    }
}
