package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JpaCustomerDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private JpaCustomerDetailsService cut;

    @Test
    void shouldFindAndReturnCustomerAsUserDetails() {
        // Given
        Customer customer = CustomerMother.complete().build();

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));

        // When
        CustomerUserDetails.SecurityCustomer customerDetails =
                (CustomerUserDetails.SecurityCustomer) cut.loadUserByUsername(customer.getEmail());

        // Then
        assertThat(customerDetails.getAuthorities().size()).isEqualTo(1);
        assertThat(customerDetails.getAuthorities().stream().toList().get(0).getAuthority())
                .isEqualTo("CUSTOMER");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenProvidedCustomerEmailDoesNotExist() {
        // Given
        String customerEmail = "duke.last@gmail.com";

        given(customerRepository.findByEmail(customerEmail)).willReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> cut.loadUserByUsername(customerEmail))
                .isInstanceOf(UsernameNotFoundException.class);
    }


}
