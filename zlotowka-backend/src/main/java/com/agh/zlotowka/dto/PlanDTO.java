package com.agh.zlotowka.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PlanDTO (
        Integer planId,
        Integer userId,
        String name,
        String description,
        BigDecimal amount,
        LocalDate date,
        Integer currencyId,
        Boolean completed,
        BigDecimal actualAmount,
        Boolean canBeCompleted,
        Double subplansCompleted
)
{}
