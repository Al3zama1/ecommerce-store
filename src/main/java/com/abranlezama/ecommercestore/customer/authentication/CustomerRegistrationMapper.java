package com.abranlezama.ecommercestore.customer.authentication;

import com.abranlezama.ecommercestore.customer.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerRegistrationMapper {

    Customer mapRegisterDtoToCustomer(RegisterCustomerDTO dto);
}
