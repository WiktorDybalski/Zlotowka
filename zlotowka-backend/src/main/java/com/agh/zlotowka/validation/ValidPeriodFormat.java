package com.agh.zlotowka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeriodFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPeriodFormat {
    String message() default "Niepoprawny format danych. Musi być w formacie ISO-8601 jak P1D, P1W, P1M, P1Y";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
