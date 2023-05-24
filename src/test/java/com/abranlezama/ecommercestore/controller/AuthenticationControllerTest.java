package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfig;
import com.abranlezama.ecommercestore.customer.AuthenticationController;
import com.abranlezama.ecommercestore.customer.dto.authentication.CustomerRegisterDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterDTOMother;
import com.abranlezama.ecommercestore.customer.CustomerAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CustomerAuthService authenticationService;

    @Test
    void shouldCallCustomerRegisterEndpoint() throws Exception {
        // Given
        CustomerRegisterDTO customerRegisterDto = RegisterDTOMother.customer().build();
        long userId = 1L;

        given(authenticationService.registerCustomer(customerRegisterDto)).willReturn(1L);

        // When
        this.mockMvc.perform(post("/api/v1/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("/customers/" + 1L)));


        // Then
        then(authenticationService).should().registerCustomer(customerRegisterDto);
    }

}
