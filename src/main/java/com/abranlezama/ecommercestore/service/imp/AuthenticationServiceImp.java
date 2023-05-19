package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.event.UserActivationDetails;
import com.abranlezama.ecommercestore.exception.AuthException;
import com.abranlezama.ecommercestore.exception.EmailTakenException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.UnequalPasswordsException;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.AccountActivationService;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImp  implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountActivationService  accountActivationService;

    @Override
    public void registerCustomer(RegisterCustomerDTO registerDto) {
        // ensure password and verify password match
        if (!registerDto.password().equals(registerDto.verifyPassword())) {
            throw new UnequalPasswordsException(ExceptionMessages.DIFFERENT_PASSWORDS);
        }

        // verify user with same email does not exit
        boolean existsUser = userRepository.existsByEmail(registerDto.email());
        if (existsUser) throw new EmailTakenException(ExceptionMessages.EMAIL_TAKEN);

        // generate user and customer from dto
        User user = authenticationMapper.mapToUser(registerDto);
        // TODO implement email activation
        user.setEnabled(true);
        Customer customer = authenticationMapper.mapToCustomer(registerDto);

        // encrypt user/customer password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // assign customer/user role of customer
        assignRoleToUser(user, RoleType.CUSTOMER);

        // save customer - user record will be persisted as well through cascading
        customer.setUser(user);
        customer.setCart(Cart.builder().totalCost(0F).build());
        customerRepository.save(customer);

        // send email with account activation token
        UserActivationDetails emailDetails = UserActivationDetails.builder()
                        .userEmail(user.getEmail())
                        .name(customer.getFirstName())
                        .token(UUID.randomUUID().toString())
                        .build();
        applicationEventPublisher.publishEvent(emailDetails);
    }

    private void assignRoleToUser(User user, RoleType roleType) {
        Role role = roleRepository.findByRole(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));
    }

    @Override
    public String authenticateUser(AuthenticationRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            return tokenService.generateJwt(authentication);

        } catch (AuthenticationException ex) {
            throw new AuthException(ExceptionMessages.AUTHENTICATION_FAILED);
        }
    }

    @Override
    public void activateUserAccount(String token) {

    }
}
