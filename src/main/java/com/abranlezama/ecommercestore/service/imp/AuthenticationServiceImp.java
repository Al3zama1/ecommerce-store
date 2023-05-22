package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.RequestActivationTokenDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.event.UserActivationDTO;
import com.abranlezama.ecommercestore.exception.*;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserActivationRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.TokenService;
import com.abranlezama.ecommercestore.utils.ResponseMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImp  implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserActivationRepository userActivationRepository;
    private final Clock clock;

    @Override
    public void registerCustomer(RegisterCustomerDTO registerDto) {
        // register user
        User user = registerUser(registerDto, RoleType.CUSTOMER);

        // generate customer from dto
        Customer customer = authenticationMapper.mapToCustomer(registerDto);

        // register customer
        customer.setUser(user);
        customer.setCart(Cart.builder().totalCost(0F).build());
        customerRepository.save(customer);

        // generate and send account activation event
        String token = generateAccountActivationToken(user);
        sendAccountActivationEmail(user, customer, token);
    }

    private User registerUser(RegisterCustomerDTO registerDto, RoleType roleType) {
        // verify password and verifyPassword match
        if (!registerDto.password().equals(registerDto.verifyPassword())) {
            throw new UnequalPasswordsException(ExceptionMessages.DIFFERENT_PASSWORDS);
        }

        // verify user with same email does not exist already
        boolean existsUser = userRepository.existsByEmail(registerDto.email());
        if (existsUser) throw new EmailTakenException(ExceptionMessages.EMAIL_TAKEN);

        // generate user from dto
        User user = authenticationMapper.mapToUser(registerDto);

        // encrypt user password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // assign role to user
        assignRoleToUser(user, roleType);

        return userRepository.save(user);
    }

    @Override
    public String authenticateUser(AuthenticationDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        return tokenService.generateJwt(authentication);
    }

    @Override
    public void activateUserAccount(String token) {
        // Verify existence of token
        UserActivation userActivation = userActivationRepository
                .findById(UUID.fromString(token))
                .orElseThrow(() -> new AccountActivationException(ExceptionMessages.INVALID_ACTIVATION_TOKEN));

        // set user enabled to true
        User user = userActivation.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // remove token
        userActivationRepository.delete(userActivation);
    }

    @Override
    public String resendAccountActivationToken(RequestActivationTokenDTO requestDto) {
        // retrieve user
        Customer customer = customerRepository.findByUser_Email(requestDto.userEmail())
                .orElseThrow(() -> new UserNotFound(ExceptionMessages.USER_NOT_FOUND));
        User user = customer.getUser();

        // verify user is not enabled
        if (user.isEnabled()) return ResponseMessages.USER_IS_ENABLED;

        sendAccountActivationEmail(customer.getUser(), customer, user.getUserActivation().getToken().toString());
        return ResponseMessages.ACTIVATION_TOKEN_SENT;
    }

    private String generateAccountActivationToken(User user) {
        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .createdDate(LocalDateTime.now(clock))
                .build();
        userActivation = userActivationRepository.save(userActivation);
        return userActivation.getToken().toString();
    }

    private void sendAccountActivationEmail(User user, Customer customer, String token) {
        // send event to mailService
        UserActivationDTO emailDetails = UserActivationDTO.builder()
                .userEmail(user.getEmail())
                .name(customer.getFirstName())
                .token(token)
                .build();
        applicationEventPublisher.publishEvent(emailDetails);
    }

    private void assignRoleToUser(User user, RoleType roleType) {
        Role role = roleRepository.findByRole(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));
    }
}
