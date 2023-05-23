package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationDTO;
import com.abranlezama.ecommercestore.dto.cart.AddItemToCartDto;
import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.CartItemDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.AuthenticationDTOMother;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
public class CartControllerIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CartMapper cartMapper;
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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserActivationRepository userActivationRepository;

    @BeforeEach
    void setUp() {
        this.orderRepository.deleteAll();
        this.customerRepository.deleteAll();
        this.userActivationRepository.deleteAll();
        this.userRepository.deleteAll();
        this.productRepository.deleteAll();
    }

    @Test
    void shouldReturnCustomerCarts() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();
        String token = obtainToken(authRequest);

        // When, Then
        this.webTestClient
                .get()
                .uri("/cart")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDTO.class)
                .value(CartDTO::cartItems, Matchers.hasSize(0));
    }

    @Test
    void shouldAddProductToShoppingCart() throws Exception{
        // Given
        generateTestInfrastructure();
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();
        Product product = productRepository.save(ProductMother.complete().id(null).build());
        AddItemToCartDto addItemToCartDto = new AddItemToCartDto(product.getId(), (short) 4);
        String token = obtainToken(authRequest);

        // When
        this.webTestClient
                .post()
                .uri("/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(addItemToCartDto))
                .exchange()
                .expectStatus().isOk();

        // Then
        Cart cart = cartRepository.findByCustomer_User_Email("duke.last@gmail.com").orElseThrow();
        CartDTO cartDto = cartMapper.mapCartToDto(cart);
        CartItemDTO cartItemDto = cartDto.cartItems().stream().findFirst().get();

        assertThat(cartDto.cartItems().size()).isEqualTo(1);
        assertThat(cartItemDto.productId()).isEqualTo(addItemToCartDto.productId());
        assertThat(cartItemDto.name()).isEqualTo(product.getName());
        assertThat(cartItemDto.quantity()).isEqualTo(addItemToCartDto.quantity());
        assertThat(cartItemDto.price()).isEqualTo(product.getPrice());
        assertThat(cartDto.cartTotal()).isEqualTo(product.getPrice() * addItemToCartDto.quantity());
    }

    @Test
    void shouldUpdateCartProduct() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();
        int updatedQuantity = 3;
        Product product = productRepository.save(ProductMother.complete().id(null).build());
        Cart cart = cartRepository.findByCustomer_User_Email(authRequest.email()).orElseThrow();
        cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity((short) 1).build());
        String token = obtainToken(authRequest);

        // When
        this.webTestClient
                .patch()
                .uri("/cart?productId={productId}&quantity={quantity}", product.getId(), updatedQuantity)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();

        // Then
        cart = cartRepository.findByCustomer_User_Email("duke.last@gmail.com").orElseThrow();
        CartDTO cartDto = cartMapper.mapCartToDto(cart);
        CartItemDTO cartItemDto = cartDto.cartItems().stream().findFirst().get();

        assertThat(cartDto.cartItems().size()).isEqualTo(1);
        assertThat(cartItemDto.name()).isEqualTo(product.getName());
        assertThat(cartItemDto.quantity()).isEqualTo((short) 3);
        assertThat(cartItemDto.productId()).isEqualTo(product.getId());
        assertThat(cartDto.cartTotal()).isEqualTo(product.getPrice() * updatedQuantity);
    }

    @Test
    void shouldRemoveCartProduct() throws Exception {
        // Given
        generateTestInfrastructure();
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();

        Product product = productRepository.save(ProductMother.complete().id(null).build());
        Cart cart = cartRepository.findByCustomer_User_Email(authRequest.email()).orElseThrow();
        cartItemRepository.save(CartItem.builder().cart(cart).product(product).quantity((short) 1).build());
        String token = obtainToken(authRequest);

        // When
        this.webTestClient
                .delete()
                .uri("/cart?productId={productId}", product.getId())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();

        // Then
        cart = cartRepository.findByCustomer_User_Email("duke.last@gmail.com").orElseThrow();
        CartDTO cartDto = cartMapper.mapCartToDto(cart);

        assertThat(cartDto.cartItems().size()).isEqualTo(0);
        assertThat(cartDto.cartTotal()).isEqualTo(0);
    }

    private String obtainToken(AuthenticationDTO authRequest) throws Exception {
        EntityExchangeResult<String> response = this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        return response.getResponseBody();
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
