package com.abranlezama.ecommercestore.dto.authentication;

import com.abranlezama.ecommercestore.annotations.USPhone;
import com.abranlezama.ecommercestore.annotations.USPostalCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record RegisterCustomerDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @USPhone String phoneNumber,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        @USPostalCode String postalCode,
        @Email String email,
        @Length(min = 8, max = 15) String password
) {
}
