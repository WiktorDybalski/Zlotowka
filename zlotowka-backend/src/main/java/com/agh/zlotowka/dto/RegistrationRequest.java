package com.agh.zlotowka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        String lastName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Incorrect email format")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {}
