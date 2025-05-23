package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import com.agh.zlotowka.validation.MaxDecimalPlaces;
import com.agh.zlotowka.validation.ValidPeriodFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionRequest(
        @NotNull(message = "ID użytkownika nie może być puste")
        @Positive(message = "ID użytkownika musi być liczbą dodatnią")
        Integer userId,

        @NotBlank(message = "Nazwa nie może być pusta")
        @Size(max = 512, message = "Nazwa nie może przekraczać 512 znaków")
        String name,

        @NotNull(message = "Kwota nie może być pusta")
        @Positive(message = "Kwota musi być liczbą dodatnią")
        @MaxDecimalPlaces(2)
        BigDecimal amount,

        @NotNull(message = "ID waluty nie może być puste")
        Integer currencyId,

        @NotNull(message = "Informacja o typie transakcji nie może być pusta")
        Boolean isIncome,

        @NotBlank(message = "Okres powtarzania nie może być pusty")
        @ValidPeriodFormat
        String interval,

        @NotNull(message = "Data pierwszej płatności nie może być pusta")
        @DateAfter2000(message = "Data pierwszej płatności musi być po 2000-01-01")
        LocalDate firstPaymentDate,

        @NotNull(message = "Data ostatniej płatności nie może być pusta")
        @DateAfter2000(message = "Data ostatniej płatności musi być po 2000-01-01")
        LocalDate lastPaymentDate,

        @Size(max = 512, message = "Opis nie może przekraczać 512 znaków")
        String description
) {
}
