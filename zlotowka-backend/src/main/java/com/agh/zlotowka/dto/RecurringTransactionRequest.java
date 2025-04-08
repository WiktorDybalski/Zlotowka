package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import com.agh.zlotowka.validation.ValidPeriodFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionRequest(
        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be positive")
        Integer userId,

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 512, message = "Name cannot exceed 512 characters")
        String name,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Currency ID cannot be null")
        Integer currencyId,

        @NotNull(message = "Income flag cannot be null")
        Boolean isIncome,

        @NotBlank(message = "Interval cannot be blank")
        @ValidPeriodFormat
        String interval,

        @NotNull(message = "First payment date cannot be null")
        @DateAfter2000(message = "First payment date must be after 2000-01-01")
        LocalDate firstPaymentDate,

        @NotNull(message = "Last payment date cannot be null")
        @DateAfter2000(message = "Last payment date must be after 2000-01-01")
        LocalDate lastPaymentDate,

        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description
) {
}
