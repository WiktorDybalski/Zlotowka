package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponse(
        Integer userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfJoining,
        BigDecimal currentBudget,
        Currency currency,
        Boolean darkMode,
        Boolean notificationsByEmail,
        Boolean notificationsByPhone
) {}
