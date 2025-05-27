package com.agh.zlotowka.dto;

public record UserDetailsRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String darkMode,
        String notificationsByEmail,
        String notificationsByPhone
) {
}
