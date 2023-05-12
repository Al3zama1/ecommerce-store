package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtDecoder jwtDecoder;

    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("ecommerce")
            .withPassword("test")
            .withUsername("tes");
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeAll
    static void beforeAll() {
        database.start();
    }

    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @Test
    void shouldRegisterAndAuthenticateCustomer() throws Exception {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete().build();
        AuthenticationRequestDTO authDto = AuthenticationRequestDTO.builder()
                        .email(registerDto.email())
                        .password(registerDto.password())
                        .build();

        // When
        mockMvc.perform(post("/auth/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/auth/login"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        Jwt jwt = jwtDecoder.decode(result.getResponse().getContentAsString());
        assertThat(jwt.getClaim("sub").toString()).isEqualTo(authDto.email());
    }

    @Test
    void shouldReturn401WhenAuthenticationCredentialsAreIncorrect() throws Exception{
        // Given
        AuthenticationRequestDTO authDto = AuthenticationRequestDTO.builder()
                .email("duke.last@gmail.com")
                .password("12345678")
                .build();

        // When
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", Matchers.is(ExceptionMessages.AUTHENTICATION_FAILED)));

    }
}
