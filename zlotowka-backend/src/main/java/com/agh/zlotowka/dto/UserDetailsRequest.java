package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserDetailsRequest(
        String firstName,
        String lastName,
        @Email(message = "Incorrect email format")
        String email,
        String phoneNumber,
        @Pattern(regexp = "^(true|false)$", message = "darkMode must be 'true' or 'false'")
        String darkMode,
        @Pattern(regexp = "^(true|false)$", message = "notificationsByEmail must be 'true' or 'false'")
        String notificationsByEmail,
        @Pattern(regexp = "^(true|false)$", message = "notificationsByPhone must be 'true' or 'false'")
        String notificationsByPhone
        ) {
}
