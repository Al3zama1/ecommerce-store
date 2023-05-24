package com.abranlezama.ecommercestore.customer;

import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.customer.mapper.CustomerMapper;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImp implements CustomerAuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public long registerCustomer(RegisterCustomerDTO registerCustomerDto) {
        // verify that provided password match
        if (!registerCustomerDto.password().equals(registerCustomerDto.verifyPassword()))
            throw new BadRequestException(ExceptionMessages.DIFFERENT_PASSWORDS);

        // verify customer is available
        boolean emailExists = customerRepository.existsByEmail(registerCustomerDto.email());
        if (emailExists) throw new ConflictException(ExceptionMessages.EMAIL_TAKEN);

        // generate customer from dto and encrypt password
        Customer customer = customerMapper.mapRegisterDtoToCustomer(registerCustomerDto);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        customer = customerRepository.save(customer);
        return customer.getId();
    }
}
