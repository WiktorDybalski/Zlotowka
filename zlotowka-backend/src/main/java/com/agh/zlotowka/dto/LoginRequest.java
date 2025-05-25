package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Adres e-mail nie może być pusty")
        @Email(message = "Nieprawidłowy format adresu e-mail")
        String email,

        @NotBlank(message = "Hasło nie może być puste")
        String password
) {}
