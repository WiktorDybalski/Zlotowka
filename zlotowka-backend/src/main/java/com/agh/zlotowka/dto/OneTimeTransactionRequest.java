package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OneTimeTransactionRequest(
        @NotNull(message = "User Id cannot be null")
        @Positive(message = "User Id must be positive")
        Integer userId,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Currency ID cannot be null")
        Integer currencyId,

        @NotNull(message = "Amount cannot be null")
        Boolean isIncome,

        @NotNull(message = "Amount cannot be null")
        @DateAfter2000(message = "Date must be after 2000-01-01")
        LocalDate date,

        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description
) {
}
