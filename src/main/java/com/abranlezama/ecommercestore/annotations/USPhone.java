package com.abranlezama.ecommercestore.annotations;

import com.abranlezama.ecommercestore.validators.USPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = USPhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface USPhone {

    String message() default "Phone number must be in format XXX-XXX-XXXX";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
