package com.agh.zlotowka.dto;

import java.time.LocalDate;

public record UserDataInDateRangeRequest(
        int userId,
        LocalDate startDate,
        LocalDate endDate
) {
}
