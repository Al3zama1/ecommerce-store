package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.dto.authentication.mapper.AuthenticationMapper;
import com.abranlezama.ecommercestore.exception.EmailTakenException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp  implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final CustomerRepository customerRepository;

    @Override
    public void registerCustomer(RegisterCustomerDTO registerCustomerDTO) {
        // verify user with same email does not exit
        boolean existsUser = userRepository.existsByEmail(registerCustomerDTO.email());
        if (existsUser) throw new EmailTakenException(ExceptionMessages.EMAIL_TAKEN);

        // generate user and customer from dto
        User user = authenticationMapper.mapToUser(registerCustomerDTO);
        Customer customer = authenticationMapper.mapToCustomer(registerCustomerDTO);

        // save customer - user record will be persisted as well through cascading
        customer.setUser(user);
        customerRepository.save(customer);
    }
}
