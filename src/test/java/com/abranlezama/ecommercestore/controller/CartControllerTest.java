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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
