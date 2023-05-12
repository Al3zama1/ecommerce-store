package com.abranlezama.ecommercestore.dto.authentication.mapper;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AuthenticationMapper {


    User mapToEntity(RegisterCustomerDTO dto);

    Customer mapToCustomer(RegisterCustomerDTO dto);
}
