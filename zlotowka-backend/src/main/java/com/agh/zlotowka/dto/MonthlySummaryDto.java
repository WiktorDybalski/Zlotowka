package com.agh.zlotowka.dto;

import java.math.BigDecimal;

public record MonthlySummaryDto(BigDecimal monthlyIncome, BigDecimal monthlyExpenses, BigDecimal monthlyBalance) {
}
