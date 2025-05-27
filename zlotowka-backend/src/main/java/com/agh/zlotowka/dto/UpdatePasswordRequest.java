package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
        String oldPassword,
        @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
        String newPassword,
        @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
        String confirmNewPassword
) {
}