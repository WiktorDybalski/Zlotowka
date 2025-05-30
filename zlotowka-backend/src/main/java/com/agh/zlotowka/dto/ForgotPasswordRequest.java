package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email nie może być pusty")
        @Email(message = "Nieprawidłowy format adresu email")
        String email
) {}
