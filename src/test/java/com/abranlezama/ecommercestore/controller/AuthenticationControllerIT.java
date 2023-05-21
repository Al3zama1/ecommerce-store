package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.ExceptionResponse;
import com.abranlezama.ecommercestore.model.Role;
import com.abranlezama.ecommercestore.model.RoleType;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.model.UserActivation;
import com.abranlezama.ecommercestore.objectmother.AuthenticationDTOMother;
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
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
public class AuthenticationControllerIT {

    @Autowired
    WebTestClient webTestClient;
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
        AuthenticationDTO authDto = AuthenticationDTOMother.complete().email("ha1838970@gmail.com").build();

        // When
        this.webTestClient
                .post()
                .uri("/auth/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(registerDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectHeader().value("Location", Matchers.is("/auth/login"));

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
    void shouldFailAuthenticationWhenEmailProvidedDoesNotMatch() throws Exception{
        // Given
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().email("lol@gmail.com").build();
        User user = UserMother.complete().isEnabled(true).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // When, Then
        this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ExceptionResponse.class)
                .value(ExceptionResponse::getMessage, Matchers.is(ExceptionMessages.AUTHENTICATION_FAILED));
    }

    @Test
    void shouldFailAuthenticationWhenPasswordProvidedDoesNotMatch() throws Exception{
        // Given
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().password("123456789").build();
        User user = UserMother.complete().isEnabled(true).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // When, Then
        this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ExceptionResponse.class)
                .value(ExceptionResponse::getMessage, Matchers.is(ExceptionMessages.AUTHENTICATION_FAILED));
    }

    @Test
    void shouldBlockLoginAttemptForAccountsNotEnabled() throws Exception {
        // Given
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();
        User user = UserMother.complete().build();
        userRepository.save(user);

        // When, Then
        this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ExceptionResponse.class)
                .value(ExceptionResponse::getMessage, Matchers.is(ExceptionMessages.ACTIVATE_ACCOUNT));
    }

    @Test
    void shouldAuthenticateUserAndReturnJWTToken() throws Exception{
        // Given
        Role role = roleRepository.findByRole(RoleType.CUSTOMER).orElseThrow();
        User user = UserMother.complete().isEnabled(true).roles(Set.of(role)).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AuthenticationDTO authRequest = AuthenticationDTOMother.complete().build();

        userRepository.save(user);

        // Whe
        FluxExchangeResult<String> response = this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class);

        Jwt jwt = jwtDecoder.decode(response.getResponseBody().blockFirst());
        assertThat(jwt.getClaim("sub").toString()).isEqualTo(authRequest.email());
    }

    @Test
    void shouldReturn401WhenAuthenticationCredentialsAreIncorrect() throws Exception{
        // Given
        AuthenticationDTO authDto = AuthenticationDTOMother.complete().build();

        // When, Then
        this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authDto))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ExceptionResponse.class)
                .value(ExceptionResponse::getMessage, Matchers.is(ExceptionMessages.AUTHENTICATION_FAILED));
    }

    @Test
    void shouldActivateUserAccount() throws Exception{
        // Given
        Role role = roleRepository.findByRole(RoleType.CUSTOMER).orElseThrow();
        User user = UserMother.complete().roles(Set.of(role)).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        UserActivation userActivation = UserActivation.builder().user(user).createdDate(LocalDateTime.now()).build();
        userActivation = userActivationRepository.save(userActivation);

        // When
        this.webTestClient
                .get()
                .uri("/auth/activate-account?token={token}", userActivation.getToken().toString())
                .exchange()
                .expectStatus().isOk();

        // Then
        user = userRepository.findByEmail(user.getEmail()).orElseThrow();
        Optional<UserActivation> userActivationOptional = userActivationRepository.findById(userActivation.getToken());

        assertThat(user.isEnabled()).isTrue();
        assertThat(userActivationOptional.isEmpty()).isTrue();
    }
}
