package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.config.SecurityConfig;
import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
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
        RegisterCustomerDTO registerCustomerDto = RegisterCustomerDTOMother.complete().build();
        long userId = 1L;

        given(authenticationService.register(registerCustomerDto)).willReturn(1L);

        // When
        this.mockMvc.perform(post("/api/v1/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCustomerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("/customers/" + 1L)));


        // Then
        then(authenticationService).should().register(registerCustomerDto);
    }


    @Test
    void shouldAuthenticateCustomer() throws Exception {
        // Given
        AuthenticationDTO authDto = new AuthenticationDTO("duke.last@gmail.com", "12345678");

        // When
        this.mockMvc.perform(post("/api/v1/login/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk());

        // Then
        then(authenticationService).should().authenticate(authDto);
    }
}
