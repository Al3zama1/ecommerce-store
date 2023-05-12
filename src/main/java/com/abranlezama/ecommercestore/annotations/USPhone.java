package com.abranlezama.ecommercestore.annotations;

import com.abranlezama.ecommercestore.validators.USPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = USPhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface USPhone {

    String message() default "Phone number must be in format XXX-XXX-XXXX";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
