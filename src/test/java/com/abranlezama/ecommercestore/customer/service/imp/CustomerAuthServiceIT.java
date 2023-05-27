package com.abranlezama.ecommercestore.customer.service.imp;

import com.abranlezama.ecommercestore.PostgresContainerConfig;
import com.abranlezama.ecommercestore.customer.Customer;
import com.abranlezama.ecommercestore.customer.repository.CustomerRepository;
import com.abranlezama.ecommercestore.customer.service.CustomerAuthService;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("integration-test")
@Import(PostgresContainerConfig.class)
@DisplayName("customer authentication service IT")
public class CustomerAuthServiceIT {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerAuthService customerAuthService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("customer authentication and jwt generation")
    void shouldAuthenticateCustomerAndGenerateValidJwtToken() {
        // Given
        AuthenticationDTO authDto = new AuthenticationDTO("duke.last@gmail.com", "12345678");

        Customer customer = CustomerMother.complete().enabled(true).build();
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);

        // When
        String token = customerAuthService.authenticate(authDto);

        // Then
        Jwt jwt = jwtDecoder.decode(token);
        assertThat(jwt.getSubject()).isEqualTo(authDto.email());
    }
}
