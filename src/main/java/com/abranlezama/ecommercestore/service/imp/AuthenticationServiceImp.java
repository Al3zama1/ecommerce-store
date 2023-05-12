package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.exception.EmailTakenException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.UnequalPasswordsException;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp  implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

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
        Customer customer = authenticationMapper.mapToCustomer(registerDto);

        // encrypt user/customer password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // save customer - user record will be persisted as well through cascading
        customer.setUser(user);
        customerRepository.save(customer);
    }
}
