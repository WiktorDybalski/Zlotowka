package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.PeriodEnum;
import com.agh.zlotowka.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(
        Integer transactionId,
        Integer userId,
        String name,
        BigDecimal amount,
        Currency currency,
        Boolean isIncome,
        LocalDate date,
        String description,
        PeriodEnum period
) {
}
