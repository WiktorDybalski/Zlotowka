package com.agh.zlotowka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;


public record ResetPasswordRequest(
        @NotBlank @Email
        String email,

        @NotBlank(message = "Token nie może być pusty")
        String token,

        @NotBlank(message = "Hasło nie może być puste")
        @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
        String newPassword
) {}
