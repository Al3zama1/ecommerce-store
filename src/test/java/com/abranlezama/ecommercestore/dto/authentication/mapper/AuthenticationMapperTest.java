package com.abranlezama.ecommercestore.dto.authentication.mapper;

import com.abranlezama.ecommercestore.dto.authentication.RegisterCustomerDTO;
import com.abranlezama.ecommercestore.model.User;
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

}
