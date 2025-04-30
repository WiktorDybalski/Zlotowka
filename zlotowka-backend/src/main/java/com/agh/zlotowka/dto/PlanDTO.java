package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanDTO (
        Integer planId,
        Integer userId,
        String name,
        String description,
        BigDecimal amount,
        LocalDate date,
        Currency currency,
        Boolean completed,
        BigDecimal actualAmount,
        Boolean canBeCompleted,
        Double subplansCompleted
)
{}
