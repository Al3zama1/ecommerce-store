package com.abranlezama.ecommercestore.customer.mapper;

import com.abranlezama.ecommercestore.customer.Customer;
import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerRegistrationMapper {

    Customer mapRegisterDtoToCustomer(RegisterCustomerDTO dto);
}
