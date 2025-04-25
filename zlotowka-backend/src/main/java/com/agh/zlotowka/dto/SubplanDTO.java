package com.agh.zlotowka.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubplanDTO (
        Integer planId,
        Integer subplanId,
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
