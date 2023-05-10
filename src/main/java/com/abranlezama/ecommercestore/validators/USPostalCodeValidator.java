package com.abranlezama.ecommercestore.validators;

import com.abranlezama.ecommercestore.annotations.USPostalCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class USPostalCodeValidator implements ConstraintValidator<USPostalCode, String> {

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{5}(?:-\\d{4})?$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;

        return POSTAL_CODE_PATTERN.matcher(value).matches();
    }
}
