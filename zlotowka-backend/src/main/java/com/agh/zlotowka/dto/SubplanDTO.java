package com.agh.zlotowka.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        LocalDate date
)
{}
