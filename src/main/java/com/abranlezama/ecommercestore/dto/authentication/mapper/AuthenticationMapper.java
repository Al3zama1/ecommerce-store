package com.abranlezama.ecommercestore.dto.authentication.mapper;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface AuthenticationMapper {


    User mapToUser(RegisterCustomerDTO dto);

    Customer mapToCustomer(RegisterCustomerDTO dto);
}
