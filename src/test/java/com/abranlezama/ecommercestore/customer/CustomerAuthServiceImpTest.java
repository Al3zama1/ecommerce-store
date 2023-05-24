package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.customer.mapper.CustomerMapper;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerAuthServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;
    @InjectMocks
    private CustomerAuthServiceImp cut;

    @Test
    void shouldRegisterCustomer() {
        // Given
        RegisterCustomerDTO registerDto = RegisterCustomerDTOMother.complete().build();
        Customer customer = CustomerMother.complete().build();

        given(customerRepository.existsByEmail(registerDto.email())).willReturn(false);
        given(customerMapper.mapRegisterDtoToCustomer(registerDto)).willReturn(customer);
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
}
