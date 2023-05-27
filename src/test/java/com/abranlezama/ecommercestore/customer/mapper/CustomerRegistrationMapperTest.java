package com.abranlezama.ecommercestore.customer.mapper;

import com.abranlezama.ecommercestore.customer.model.Customer;
import com.abranlezama.ecommercestore.customer.dto.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegistrationDTO mapping to customer")
class CustomerRegistrationMapperTest {

    CustomerRegistrationMapper mapper = Mappers.getMapper(CustomerRegistrationMapper.class);

    @Test
    @DisplayName("map registration dto to customer")
    void shouldConvertRegisterCustomerDTOToCustomerEntity() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTOMother.complete().build();

        // When
        Customer customer = mapper.mapRegisterDtoToCustomer(dto);

        // Then
        assertThat(customer.getName()).isEqualTo(dto.name());
        assertThat(customer.getEmail()).isEqualTo(dto.email());
        assertThat(customer.getPassword()).isEqualTo(dto.password());
    }

}
