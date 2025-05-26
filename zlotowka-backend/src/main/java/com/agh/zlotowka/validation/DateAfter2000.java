package com.agh.zlotowka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateAfter2000Validator.class)
public @interface DateAfter2000 {
    String message() default "Data musi być po 2000-01-01";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
