package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record OneTimeTransactionDTO(
        Integer transactionId,
        Integer userId,
        String name,
        BigDecimal amount,
        Currency currency,
        Boolean isIncome,
        LocalDate date,
        String description
) {

}
