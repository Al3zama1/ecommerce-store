package com.abranlezama.ecommercestore.customer.controller;

import com.abranlezama.ecommercestore.config.SecurityConfig;
import com.abranlezama.ecommercestore.customer.service.CustomerAuthService;
import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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


@WebMvcTest(CustomerAuthController.class)
@Import(SecurityConfig.class)
class CustomerAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CustomerAuthService authenticationService;

    @Nested
    @DisplayName("creating customer")
    class CustomerCreation {

        @Nested
        @DisplayName("when fields are valid")
        class ValidInput {

            @Test
            @DisplayName("Return HTTP status created")
            void returnHttpStatusCreated() throws Exception {
                // Given
                RegisterCustomerDTO registerCustomerDto = RegisterCustomerDTOMother.complete().build();
                long userId = 1L;

                given(authenticationService.register(registerCustomerDto)).willReturn(1L);

                // When
                mockMvc.perform(post("/api/v1/register/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerCustomerDto)))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location", is("/customers/" + 1L)));

                // Then
                then(authenticationService).should().register(registerCustomerDto);
            }
        }

        @Nested
        @DisplayName("when fields are invalid")
        class WhenFieldsAreMissing {

            @Test
            @DisplayName("Return HTTP status UnprocessableEntity(422)")
            void returnHttpStatusUnprocessableEntity() throws Exception {
                // Given
                RegisterCustomerDTO registerCustomerDto = RegisterCustomerDTOMother.complete()
                        .email("john.com")
                        .build();
                long userId = 1L;

                given(authenticationService.register(registerCustomerDto)).willReturn(1L);

                // When
                mockMvc.perform(post("/api/v1/register/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerCustomerDto)))
                        .andExpect(status().isUnprocessableEntity());

                // Then
                then(authenticationService).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("customer authentication")
    class CustomerAuthentication {

        @Nested
        @DisplayName("when fields are valid")
        class WhenValidFields {
            @Test
            @DisplayName("return HTTP status OK(200) with jwt token")
            void returnHttpStatusOkWithJwtToken() throws Exception {
                // Given
                AuthenticationDTO authDto = new AuthenticationDTO("duke.last@gmail.com", "12345678");

                // When
                mockMvc.perform(post("/api/v1/login/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authDto)))
                        .andExpect(status().isOk());

                // Then
                then(authenticationService).should().authenticate(authDto);
            }
        }

        @Nested
        @DisplayName("when fields are invalid")
        class WhenInvalidFields {
            @Test
            @DisplayName("return HTTP status UnprocessableEntity(422)")
            void returnHttpStatusOkWithJwtToken() throws Exception {
                // Given
                AuthenticationDTO authDto = new AuthenticationDTO("duke.last@gmail.com", "1234567");

                // When
                mockMvc.perform(post("/api/v1/login/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authDto)))
                        .andExpect(status().isUnprocessableEntity());

                // Then
                then(authenticationService).shouldHaveNoInteractions();
            }
        }
    }

}
