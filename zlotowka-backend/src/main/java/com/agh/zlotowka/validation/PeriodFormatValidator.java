package com.agh.zlotowka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.format.DateTimeParseException;
import java.time.Period;

public class PeriodFormatValidator implements ConstraintValidator<ValidPeriodFormat, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            Period.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
