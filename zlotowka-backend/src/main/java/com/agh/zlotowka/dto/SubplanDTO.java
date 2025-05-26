package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubplanDTO (
        Integer planId,
        Integer subplanId,
        String name,
        String description,
        BigDecimal amount,
        Currency currency,
        Boolean completed,
        BigDecimal actualAmount,
        Boolean canBeCompleted,
        LocalDate date,
        LocalDate estimatedCompletionDate
)
{}
