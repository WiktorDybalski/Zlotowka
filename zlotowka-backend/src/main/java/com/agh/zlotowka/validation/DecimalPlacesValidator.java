package com.agh.zlotowka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class DecimalPlacesValidator implements ConstraintValidator<MaxDecimalPlaces, BigDecimal> {

    private int maxPlaces;

    @Override
    public void initialize(MaxDecimalPlaces constraintAnnotation) {
        this.maxPlaces = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.scale() <= maxPlaces;
    }
}

