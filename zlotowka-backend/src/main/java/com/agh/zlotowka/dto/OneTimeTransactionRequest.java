package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.DateAfter2000;
import com.agh.zlotowka.validation.MaxDecimalPlaces;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OneTimeTransactionRequest(
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

        @NotNull(message = "Data płatności nie może być pusta")
        @DateAfter2000
        LocalDate date,

        @Size(max = 512, message = "Opis nie może przekraczać 512 znaków")
        String description
) {
}