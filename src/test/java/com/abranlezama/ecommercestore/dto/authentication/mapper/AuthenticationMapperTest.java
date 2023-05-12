package com.abranlezama.ecommercestore.dto.authentication.mapper;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.objectmother.RegisterCustomerDTOMother;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationMapperTest {

    private final AuthenticationMapper mapper = Mappers.getMapper(AuthenticationMapper.class);

    @Test
    void shouldAssignFieldsToUserFromDto() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTO.builder().build();

        // When
        User user = mapper.mapToEntity(dto);

        // Then
        assertThat(user.getEmail()).isEqualTo(dto.email());
        assertThat(user.getPassword()).isEqualTo(dto.password());
        assertThat(user.getId()).isEqualTo(null);
    }

    @Test
    void shouldAssignPropertiesFromCustomerRegisterDtoToCustomer() {
        // Given
        RegisterCustomerDTO dto = RegisterCustomerDTOMother.complete().build();

        // When
        Customer customer = mapper.mapToCustomer(dto);

        // Then
        assertThat(customer.getFirstName()).isEqualTo(dto.firstName());
        assertThat(customer.getLastName()).isEqualTo(dto.lastName());
        assertThat(customer.getStreet()).isEqualTo(dto.street());
        assertThat(customer.getCity()).isEqualTo(dto.city());
        assertThat(customer.getState()).isEqualTo(dto.state());
        assertThat(customer.getPostalCode()).isEqualTo(dto.postalCode());
        assertThat(customer.getId()).isEqualTo(null);

    }

}
