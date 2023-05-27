package com.abranlezama.ecommercestore.order.controller;

import com.abranlezama.ecommercestore.config.CustomerSecurityConfig;
import com.abranlezama.ecommercestore.order.service.CustomerOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerOrderController.class)
@Import(CustomerSecurityConfig.class)
@DisplayName("customer order service")
class CustomerOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerOrderService customerOrderService;
    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Nested
    @DisplayName("customer order creation")
    class OrderCreation {

        @Test
        @DisplayName("return HTTP status 201 when customer order is created")
        @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
        void returnHttpStatus201WhenCustomerOrderIsCreated() throws Exception {
            // Given
            String customerEmail = "duke.last@gmail.com";
            long orderId = 1L;

            given(customerOrderService.createCustomerOrder(customerEmail)).willReturn(orderId);

            // When
            mockMvc.perform(post("/api/v1/customers/orders"))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/customers/orders/" + orderId)));

            // Then
            then(customerOrderService).should().createCustomerOrder(customerEmail);
        }

        @Test
        @DisplayName("return HTTP status 401 when not authenticated")
        void returnHttpStatus401WhenCreatingCustomerOrderUnauthenticated() throws Exception {
            // Given

            // When
            mockMvc.perform(post("/api/v1/customers/orders"))
                    .andExpect(status().isUnauthorized());

            // Then
            then(customerOrderService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("return HTTP status 403 when not authorized")
        @WithMockUser(username = "duke.last@gmail.com", roles = "EMPLOYEE")
        void returnHttpStatus403WhenNotAuthorizedToCreateCustomerOrder() throws Exception {
            // Given

            // When
            mockMvc.perform(post("/api/v1/customers/orders"))
                    .andExpect(status().isForbidden());

            // Then
            then(customerOrderService).shouldHaveNoInteractions();
        }
    }
}
