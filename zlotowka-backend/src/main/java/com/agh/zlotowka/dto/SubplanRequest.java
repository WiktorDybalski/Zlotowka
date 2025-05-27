package com.agh.zlotowka.dto;

import com.agh.zlotowka.validation.MaxDecimalPlaces;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SubplanRequest (
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

        @Size(max = 512, message = "Opis nie może przekraczać 512 znaków")
        String description,

        @NotNull(message = "ID planu nie może być puste")
        @Positive(message = "ID planu musi być liczbą dodatnią")
        Integer planId
) {
}