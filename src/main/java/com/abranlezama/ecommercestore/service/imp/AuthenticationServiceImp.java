package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.exception.AuthException;
import com.abranlezama.ecommercestore.exception.EmailTakenException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.UnequalPasswordsException;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImp  implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    public void registerCustomer(RegisterCustomerDTO registerDto) {
        // verify password and verify password match
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

        // save customer - user record will be persisted as well through cascading
        customer.setUser(user);
        customer.setCart(Cart.builder().totalCost(0F).build());
        customerRepository.save(customer);
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
}
