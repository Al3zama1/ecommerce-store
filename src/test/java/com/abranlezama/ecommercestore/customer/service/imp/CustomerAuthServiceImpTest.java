package com.abranlezama.ecommercestore.customer.service.imp;

import com.abranlezama.ecommercestore.cart.repository.CartRepository;
import com.abranlezama.ecommercestore.customer.model.Customer;
import com.abranlezama.ecommercestore.customer.repository.CustomerRepository;
import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.customer.mapper.CustomerRegistrationMapper;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
import com.abranlezama.ecommercestore.jwttoken.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("customer authentication service")
class CustomerAuthServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerRegistrationMapper customerRegistrationMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;
    @InjectMocks
    private CustomerAuthServiceImp cut;

    @Test
    @DisplayName("register customer")
    void shouldRegisterCustomer() {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete().build();
        Customer customer = CustomerMother.complete().build();

        given(customerRepository.existsByEmail(registerDto.email())).willReturn(false);
        given(customerRegistrationMapper.mapRegisterDtoToCustomer(registerDto)).willReturn(customer);
        given(customerRepository.save(customer)).willAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setId(1L);
            return savedCustomer;
        });

        // When
        cut.register(registerDto);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        assertThat(customerArgumentCaptor.getValue().isEnabled()).isFalse();
    }

    @Test
    @DisplayName("cancel customer registration due to not matching passwords")
    void shouldThrowBadRequestExceptionWhenProvidedPasswordsDoNotMatch() {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete()
                .password("123456789")
                .build();

        // When
        assertThatThrownBy(() -> cut.register(registerDto))
                .hasMessage(ExceptionMessages.DIFFERENT_PASSWORDS)
                .isInstanceOf(BadRequestException.class);

        // Then
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("cancel customer registration when email conflict")
    void shouldThrowConflictExceptionWhenRegisterEmailIsTaken() {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete().build();

        given(customerRepository.existsByEmail(registerDto.email())).willReturn(true);

        // When
        assertThatThrownBy(() -> cut.register(registerDto))
                .hasMessage(ExceptionMessages.EMAIL_TAKEN)
                .isInstanceOf(ConflictException.class);

        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("authenticate customer with valid credentials")
    void shouldAuthenticateCustomerWithValidCredentials() {
        // Given
        AuthenticationDTO authDto = new AuthenticationDTO("duke.last@gmail.com", "12345678");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authDto.email(), authDto.password());
        // When
        cut.authenticate(authDto);

        // Then
        then(authenticationManager).should().authenticate(token);
    }
}

