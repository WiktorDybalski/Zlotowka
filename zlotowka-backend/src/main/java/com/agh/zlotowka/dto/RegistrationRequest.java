package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Imię nie może być puste")
        String firstName,

        @NotBlank(message = "Nazwisko nie może być puste")
        String lastName,

        @NotBlank(message = "Email nie może być pusty")
        @Email(message = "Nieprawidłowy format adresu email")
        String email,

        @NotBlank(message = "Hasło nie może być puste")
        @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
        String password
) {}