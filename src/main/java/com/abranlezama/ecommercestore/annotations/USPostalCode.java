package com.abranlezama.ecommercestore.annotations;

import com.abranlezama.ecommercestore.validators.USPostalCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = USPostalCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface USPostalCode {

    String message() default "US postal code must be in format XXXXX or XXXXX-XXXX";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
