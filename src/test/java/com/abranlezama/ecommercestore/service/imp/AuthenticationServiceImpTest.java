package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.RequestActivationTokenDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.exception.*;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.AuthenticationRequestDTOMother;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserActivationRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.TokenService;
import com.abranlezama.ecommercestore.utils.ResponseMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImpTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AuthenticationMapper authenticationMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private TokenService tokenService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserActivationRepository userActivationRepository;
    @Mock
    private Clock clock;
    @Mock
    private UserActivation userActivation;
    @InjectMocks
    private AuthenticationServiceImp cut;

    // Customer registration
    @Test
    void ShouldCreateRecordForUserAndCustomerFromRegisterCustomerDTO() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTOMother.complete().build();
        User user = UserMother.complete().build();
        UUID uuidToken = UUID.randomUUID();
        LocalDateTime defaultDatetime = LocalDateTime.now();
        Clock fixedclock = Clock.fixed(defaultDatetime.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

        UserActivation userActivation = UserActivation.builder()
                .createdDate(defaultDatetime)
                .user(user)
                .build();

        given(userRepository.existsByEmail(dto.email())).willReturn(false);
        given(authenticationMapper.mapToUser(dto)).willReturn(user);
        given(authenticationMapper.mapToCustomer(dto)).willReturn(CustomerMother.complete().build());
        given(roleRepository.findByRole(RoleType.CUSTOMER)).willReturn(Optional.of(new Role()));
        given(clock.instant()).willReturn(fixedclock.instant());
        given(clock.getZone()).willReturn(fixedclock.getZone());
        given(userActivationRepository.save(userActivation)).willAnswer(invocation -> {
            UserActivation savedUserActivation = invocation.getArgument(0);
            savedUserActivation.setToken(uuidToken);
            return savedUserActivation;
        });

        // When
        cut.registerCustomer(dto);


        // Then
        then(passwordEncoder).should().encode(dto.password());
        then(customerRepository).should().save(any());
    }

    @Test
    void shouldThrowUnequalPasswordsExceptionWhenCustomerRegistersWithDifferentPasswords() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTOMother.complete()
                .verifyPassword("123456789")
                .build();

        assertThatThrownBy(() -> cut.registerCustomer(dto))
                .hasMessage(ExceptionMessages.DIFFERENT_PASSWORDS)
                .isInstanceOf(UnequalPasswordsException.class);

        // Then
        then(userRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowEmailTakenExceptionWith409WhenCustomerRegistersWithAnExistingEmail() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTOMother.complete().build();

        given(userRepository.existsByEmail(dto.email())).willReturn(true);

        // When
        assertThatThrownBy(() -> cut.registerCustomer(dto))
                .hasMessage(ExceptionMessages.EMAIL_TAKEN)
                .isInstanceOf(EmailTakenException.class);

        // Then
        then(userRepository).shouldHaveNoMoreInteractions();
        then(customerRepository).shouldHaveNoInteractions();
    }

    // User authentication
    @Test
    void shouldAuthenticateUserWhenCredentialsAreCorrect() {
        // Given
        AuthenticationRequestDTO dto = AuthenticationRequestDTOMother.complete().build();

        // When
        cut.authenticateUser(dto);

        // Then
        then(tokenService).should().generateJwt(any());
    }

    @Test
    void shouldThrowAuthExceptionWhenEmailOrPasswordAreIncorrect() {
        // Given
        AuthenticationRequestDTO dto = AuthenticationRequestDTOMother.complete().build();

        given(authenticationManager.authenticate(any())).willThrow(new UsernameNotFoundException("User not found"));

        // When
        assertThatThrownBy(() -> cut.authenticateUser(dto))
                .hasMessage(ExceptionMessages.AUTHENTICATION_FAILED)
                .isInstanceOf(AuthException.class);
    }

    @Test
    void shouldActivateUserAccount() {
        // Given
        UUID token = UUID.randomUUID();
        User user = UserMother.complete().build();
        UserActivation userActivation = UserActivation.builder().user(user).token(token).build();

        given(userActivationRepository.findById(token)).willReturn(Optional.of(userActivation));

        // When
        cut.activateUserAccount(token.toString());

        // Then
        then(userActivationRepository).should().delete(userActivation);
        then(userRepository).should().save(user);
    }

    @Test
    void shouldThrowAccountActivationExceptionWhenTokenIsInvalid() {
        // Given
        UUID token = UUID.randomUUID();
        UserActivation userActivation = UserActivation.builder().token(token).build();

        given(userActivationRepository.findById(token)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.activateUserAccount(token.toString()))
                .hasMessage(ExceptionMessages.INVALID_ACTIVATION_TOKEN)
                        .isInstanceOf(AccountActivationException.class);

        // Then
        then(userRepository).shouldHaveNoInteractions();
        then(userActivationRepository).should(never()).delete(any());
    }

    @Test
    void shouldResendAccountActivationToken() {
        // Given
        RequestActivationTokenDTO request = new RequestActivationTokenDTO("duke.last@gmail.com");
        User user = UserMother.complete().isEnabled(false).build();
        UserActivation userActivation = UserActivation.builder().token(UUID.randomUUID()).build();
        user.setUserActivation(userActivation);
        Customer customer = CustomerMother.complete().user(user).build();

        given(customerRepository.findByUser_Email(request.userEmail())).willReturn(Optional.of(customer));

        // When
        String response = cut.resendAccountActivationToken(request);

        // Then
        assertThat(response).isEqualTo(ResponseMessages.ACTIVATION_TOKEN_SENT);
    }

}
