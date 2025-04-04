package com.agh.zlotowka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeriodFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPeriodFormat {
    String message() default "Invalid period format. Must be in ISO-8601 format like P1D, P1W, P1M, P1Y";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}