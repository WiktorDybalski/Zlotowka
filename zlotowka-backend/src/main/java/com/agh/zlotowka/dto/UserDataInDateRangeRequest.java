package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UserDataInDateRangeRequest(
        @NotNull(message = "ID użytkownika nie może być puste")
        @Positive(message = "ID użytkownika musi być liczbą dodatnią")
        Integer userId,

        @NotNull(message = "Data początkowa nie może być pusta")
        @DateAfter2000
        LocalDate startDate,

        @NotNull(message = "Data końcowa nie może być pusta")
        @DateAfter2000
        LocalDate endDate
) {
}