package com.abranlezama.ecommercestore.validators;

import com.abranlezama.ecommercestore.annotations.USPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class USPhoneValidator implements ConstraintValidator<USPhone, String> {

    private static final Pattern PHONE_PATTERN =  Pattern.compile("^[2-9]\\d{2}-\\d{3}-\\d{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;

        return PHONE_PATTERN.matcher(value).matches();
    }
}
