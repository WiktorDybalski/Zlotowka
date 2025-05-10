package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String oldPassword,
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String newPassword,
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String confirmNewPassword
) {
}
