package com.agh.zlotowka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateAfter2000Validator implements ConstraintValidator<DateAfter2000, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.isAfter(LocalDate.of(2000, 1, 1));
    }

    @Override
    public void initialize(DateAfter2000 constraintAnnotation) {
    }
}
