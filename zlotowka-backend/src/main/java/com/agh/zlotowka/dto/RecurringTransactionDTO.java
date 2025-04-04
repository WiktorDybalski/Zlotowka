package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.PeriodEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record RecurringTransactionDTO (
    Integer transactionId,
    Integer userId,
    String name,
    BigDecimal amount,
    Currency currency,
    Boolean isIncome,
    LocalDate firstPaymentDate,
    LocalDate nextPaymentDate,
    LocalDate finalPaymentDate,
    PeriodEnum interval,
    String description
) {

}
