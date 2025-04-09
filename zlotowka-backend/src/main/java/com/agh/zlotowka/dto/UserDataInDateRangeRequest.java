package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UserDataInDateRangeRequest(
        @NotNull(message = "User Id cannot be null")
        @Positive(message = "User Id must be positive")
        Integer userId,

        @NotNull(message = "Payment date cannot be null")
        @DateAfter2000
        LocalDate startDate,

        @NotNull(message = "Payment date cannot be null")
        @DateAfter2000
        LocalDate endDate
) {
}
