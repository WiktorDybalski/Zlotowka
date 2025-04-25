package com.agh.zlotowka.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PlanDTO (
        Integer planId,
        Integer userId,
        String name,
        String description,
        BigDecimal amount,
        Integer currencyId,
        Boolean completed,
        BigDecimal actualAmount,
        Boolean canBeCompleted,
        Double subplansCompleted
)
{}
