package com.abranlezama.ecommercestore.customer.authentication;

import com.abranlezama.ecommercestore.customer.Customer;
import com.abranlezama.ecommercestore.customer.CustomerRepository;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.sharedto.AuthenticationDTO;
import com.abranlezama.ecommercestore.jwttoken.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImp implements CustomerAuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final CustomerRegistrationMapper customerRegistrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public long register(RegisterCustomerDTO registerCustomerDto) {
        // verify that provided password match
        if (!registerCustomerDto.password().equals(registerCustomerDto.verifyPassword()))
            throw new BadRequestException(ExceptionMessages.DIFFERENT_PASSWORDS);

        // verify customer is available
        boolean emailExists = customerRepository.existsByEmail(registerCustomerDto.email());
        if (emailExists) throw new ConflictException(ExceptionMessages.EMAIL_TAKEN);

        // generate customer from dto and encrypt password
        Customer customer = customerRegistrationMapper.mapRegisterDtoToCustomer(registerCustomerDto);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        customer = customerRepository.save(customer);
        return customer.getId();
    }

    @Override
    public String authenticate(AuthenticationDTO authDto) {
        // retrieve customer associated with email
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.email(), authDto.password())
        );
        return tokenService.generateJwt(authentication);
    }
}
