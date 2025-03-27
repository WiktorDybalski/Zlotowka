package com.agh.zlotowka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OneTimeTransactionRequest(
        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be positive")
        Integer transactionId,

        @NotNull(message = "User ID cannot be null")
        @Positive(message = "User ID must be positive")
        Integer userId,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        @NotNull(message = "Currency ID cannot be null")
        Integer currencyId,

        @NotNull(message = "Amount cannot be null")
        Boolean income,

        @NotNull(message = "Amount cannot be null")
        LocalDate date,

        @NotNull(message = "Amount cannot be null")
        String description
) {
}
