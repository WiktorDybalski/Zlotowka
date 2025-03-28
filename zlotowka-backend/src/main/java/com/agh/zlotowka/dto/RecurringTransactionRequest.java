package com.agh.zlotowka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public record RecurringTransactionRequest(
        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be positive")
        Integer userId,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "User ID must be positive")
        BigDecimal amount,

        @NotNull(message = "Currency ID cannot be null")
        @Positive(message = "Currency ID must be positive")
        Integer currencyId,

        @NotNull(message = "Income flag cannot be null")
        Boolean isIncome,

        @NotBlank(message = "Interval cannot be blank")
                String interval,

        @NotNull(message = "First payment date cannot be null")
        LocalDate firstPaymentDate,

        LocalDate nextPaymentDate,

        @NotNull(message = "Last payment date cannot be null")
        LocalDate lastPaymentDate,

        String description
) {
}
