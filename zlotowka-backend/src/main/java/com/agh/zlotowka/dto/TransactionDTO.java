package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.PeriodEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO (
        Integer transactionId,
        Integer userId,
        String name,
        BigDecimal amount,
        String currencyIsoCode,
        Boolean isIncome,
        LocalDate date,
        String description,
        PeriodEnum period
) {
}
