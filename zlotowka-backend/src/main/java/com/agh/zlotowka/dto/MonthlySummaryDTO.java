package com.agh.zlotowka.dto;

import java.math.BigDecimal;

public record MonthlySummaryDTO(
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpenses,
        BigDecimal monthlyBalance,
        String currencyIsoCode
) {
}
