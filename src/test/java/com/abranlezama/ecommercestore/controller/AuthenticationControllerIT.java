package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.objectmother.AuthenticationRequestDTOMother;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserActivationRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
@AutoConfigureMockMvc
public class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    private UserActivationRepository userActivationRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
        userActivationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false); // not create new lifecycle per test

    @Test
    void shouldRegisterCustomer() throws Exception {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete().email("ha1838970@gmail.com").build();
        AuthenticationRequestDTO authDto = AuthenticationRequestDTOMother.complete().email("ha1838970@gmail.com").build();

        // When
        mockMvc.perform(post("/auth/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/auth/login"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        Awaitility.given()
                .await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                     MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
                    assertThat(receivedMessages.length).isEqualTo(1);

                    MimeMessage message = receivedMessages[0];
                    assertThat(message.getSubject()).isEqualTo("Ecommerce account activation link");
                });
    }

    @Test
    void shouldAuthenticateUserAndReturnJWTToken() throws Exception{
        // Given
        Role role = roleRepository.findByRole(RoleType.CUSTOMER).orElseThrow();
        User user = UserMother.complete().isEnabled(true).roles(Set.of(role)).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();

        userRepository.save(user);

        // Whe
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Jwt jwt = jwtDecoder.decode(result.getResponse().getContentAsString());
        assertThat(jwt.getClaim("sub").toString()).isEqualTo(authRequest.email());
    }

    @Test
    void shouldReturn401WhenAuthenticationCredentialsAreIncorrect() throws Exception{
        // Given
        AuthenticationRequestDTO authDto = AuthenticationRequestDTOMother.complete().build();

        // When
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", Matchers.is(ExceptionMessages.AUTHENTICATION_FAILED)));

    }
}
