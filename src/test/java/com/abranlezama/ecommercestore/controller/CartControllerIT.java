package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.cart.AddItemToCartDto;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.AuthenticationRequestDTOMother;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
@AutoConfigureMockMvc
public class CartControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        this.cartRepository.deleteAll();
        this.customerRepository.deleteAll();
        this.userRepository.deleteAll();
        this.productRepository.deleteAll();
    }

    @Test
    void shouldReturnCustomerCarts() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();
        String token = obtainToken(authRequest);

        // When, Then
        mockMvc.perform(get("/cart")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems.size()", Matchers.is(0)));
    }

    @Test
    void shouldAddProductToShoppingCart() throws Exception{
        // Given
        generateTestInfrastructure();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();
        Product product = productRepository.save(ProductMother.complete().id(null).build());
        AddItemToCartDto addItemToCartDto = new AddItemToCartDto(product.getId(), 4);
        String token = obtainToken(authRequest);

        // When
        this.mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addItemToCartDto)))
                .andExpect(status().isOk());

        // Then
        this.mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.cartItems[0].name", Matchers.is(product.getName())))
                .andExpect(jsonPath("$.cartItems[0].quantity", Matchers.is(4)))
                .andExpect(jsonPath("$.cartItems[0].productId", Matchers.is(product.getId().intValue())))
                .andReturn();
    }

    @Test
    void shouldUpdateCartProduct() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();

        Product product = productRepository.save(ProductMother.complete().id(null).build());
        Cart cart = cartRepository.findByCustomer_User_Email(authRequest.email()).orElseThrow();
        cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity(1).build());
        String token = obtainToken(authRequest);

        // When
        this.mockMvc.perform(patch("/cart")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", String.valueOf(product.getId()))
                        .param("quantity", String.valueOf(3)))
                .andExpect(status().isNoContent());

        // Then
        this.mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.cartItems[0].name", Matchers.is(product.getName())))
                .andExpect(jsonPath("$.cartItems[0].quantity", Matchers.is(3)))
                .andExpect(jsonPath("$.cartItems[0].productId", Matchers.is(product.getId().intValue())))
                .andReturn();
    }

    @Test
    void shouldRemoveCartProduct() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();

        Product product = productRepository.save(ProductMother.complete().id(null).build());
        Cart cart = cartRepository.findByCustomer_User_Email(authRequest.email()).orElseThrow();
        cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity(1).build());
        String token = obtainToken(authRequest);

        // When
        this.mockMvc.perform(delete("/cart")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", String.valueOf(product.getId())))
                .andExpect(status().isNoContent());

        // Then
        this.mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems.size()", Matchers.is(0)))
                .andReturn();
    }

    private String obtainToken(AuthenticationRequestDTO authRequest) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private void generateTestInfrastructure() {
        Role role = roleRepository.findByRole(RoleType.CUSTOMER).orElseThrow();
        User user = UserMother.complete().roles(Set.of(role)).isEnabled(true).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Customer customer = CustomerMother.complete()
                .user(user)
                .cart(Cart.builder().totalCost(0f).build())
                .build();
        customerRepository.save(customer);
    }
}
