package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.*;
import com.abranlezama.ecommercestore.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
public class OrderControllerIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @Test
    void shouldRetrieveCustomerOrders() {
        // Given

        // save user and associated customer
        Role role = roleRepository.findByRole(RoleType.CUSTOMER).orElseThrow();
        User user = UserMother.complete().isEnabled(true).roles(Set.of(role)).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Customer customer = CustomerMother.complete().cart(Cart.builder().totalCost(0F).build()).user(user).build();
        customerRepository.save(customer);

        // generate authentication token
        String jwtToken = generateJwtToken(user.getEmail(), "12345678");

        // generate and save product
        Product product = productRepository.save(ProductMother.complete().build());

        // generate and ave order
        OrderItem orderItem = OrderItem.builder().product(product).quantity((short) 2).build();
        OrderStatus orderStatus = orderStatusRepository.findByStatus(OrderStatusType.PROCESSING).orElseThrow();
        Order order = Order.builder()
                .customer(customer)
                .orderStatus(orderStatus)
                .orderItems(Set.of(orderItem))
                .datePlaced(LocalDateTime.now())
                .totalCost(product.getPrice() * orderItem.getQuantity())
                .build();
        order = orderRepository.save(order);

        // When
        List<OrderDTO> orders = this.webTestClient
                .get()
                .uri("/orders")
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderDTO.class)
                .returnResult()
                .getResponseBody();

        // Then
       assertThat(orders.size()).isEqualTo(1) ;
        assertThat(orders.get(0).id()).isEqualTo(order.getId());
        assertThat(orders.get(0).totalCost()).isEqualTo(order.getTotalCost());
        assertThat(orders.get(0).orderStatus()).isEqualTo(order.getOrderStatus().getStatus().name());
        assertThat(orders.get(0).datePlaced()).isEqualTo(order.getDatePlaced());
        assertThat(orders.get(0).dateShipped()).isEqualTo(order.getDateShipped());
        assertThat(orders.get(0).dateDelivered()).isEqualTo(order.getDateDelivered());
    }

    private String generateJwtToken(String userEmail, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userEmail, password)
        );

        return tokenService.generateJwt(authentication);
    }
}
