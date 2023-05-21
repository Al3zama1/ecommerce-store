package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfiguration.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;
    @MockBean
    private OrderService orderService;

    private static final String USER_EMAIL = "duke.last@gmail.com";

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "CUSTOMER")
    void shouldCallOrderServiceToCreateCustomerOrder() throws Exception {
        // Given
        int page = 0;
        int pageSize = 20;

        // When
        this.mockMvc.perform(get("/orders")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk());

        // Then
        then(orderService).should().getCustomerOrders(USER_EMAIL, page, pageSize);
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "CUSTOMER")
    void shouldCreateCustomerOrder() throws Exception {
        // Given
        long orderId = 1L;
        String userEmail = "duke.last@gmail.com";

        given(orderService.createOrder(userEmail)).willReturn(orderId);


        // When
        this.mockMvc.perform(post("/orders"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/orders/" + orderId));

        // Then
        then(orderService).should().createOrder(userEmail);
    }

}
