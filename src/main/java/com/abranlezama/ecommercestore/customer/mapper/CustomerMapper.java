package com.abranlezama.ecommercestore.customer.mapper;

import com.abranlezama.ecommercestore.customer.Customer;
import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer mapRegisterDtoToCustomer(RegisterCustomerDTO dto);
}
