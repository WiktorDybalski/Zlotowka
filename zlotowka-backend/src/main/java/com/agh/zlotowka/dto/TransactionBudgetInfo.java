package com.agh.zlotowka.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionBudgetInfo(
        String transactionName,
        LocalDate date,
        BigDecimal amount,
        boolean isIncome,
        String currencyIsoCode
) {
}
