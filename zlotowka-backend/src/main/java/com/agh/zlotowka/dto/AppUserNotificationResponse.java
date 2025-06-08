package com.agh.zlotowka.dto;

import java.time.LocalDateTime;

public record AppUserNotificationResponse(
        Integer id,
        LocalDateTime createdAt,
        String category,
        String text,
        Boolean byEmail,
        Boolean byPhone
) {}