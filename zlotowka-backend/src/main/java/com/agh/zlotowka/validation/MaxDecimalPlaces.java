package com.agh.zlotowka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DecimalPlacesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxDecimalPlaces {
    String message() default "Amount can have at most {value} decimal places";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}
