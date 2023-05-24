package com.abranlezama.ecommercestore.customer.mapper;

import com.abranlezama.ecommercestore.customer.Customer;
import com.abranlezama.ecommercestore.customer.dto.authentication.RegisterCustomerDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void shouldConvertRegisterCustomerDTOToCustomerEntity() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTO.builder()
                .name("Duke Last")
                .email("duke.last@gmail.com")
                .password("12345678")
                .verifyPassword("12345678")
                .build();

        // When
        Customer customer = mapper.mapRegisterDtoToCustomer(dto);

        // Then
        assertThat(customer.getName()).isEqualTo(dto.name());
        assertThat(customer.getEmail()).isEqualTo(dto.email());
        assertThat(customer.getPassword()).isEqualTo(dto.password());
    }

}
