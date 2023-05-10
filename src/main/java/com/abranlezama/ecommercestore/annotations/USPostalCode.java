package com.abranlezama.ecommercestore.annotations;

import com.abranlezama.ecommercestore.validators.USPhoneValidator;
import com.abranlezama.ecommercestore.validators.USPostalCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = USPostalCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface USPostalCode {

    String message() default "Invalid US phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
