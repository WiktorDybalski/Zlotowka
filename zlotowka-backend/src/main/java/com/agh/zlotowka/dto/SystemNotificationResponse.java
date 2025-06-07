package com.agh.zlotowka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SystemNotificationResponse {
    private Integer id;
    private LocalDateTime createdAt;
    private String category;
    private String text;
    private Boolean byEmail;
    private Boolean byPhone;
}
