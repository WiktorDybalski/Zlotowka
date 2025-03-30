package com.agh.zlotowka.dto;

import java.time.LocalDate;

public record UserDataInDateRangeRequest(
        Integer userId,
        LocalDate startDate,
        LocalDate endDate
) {
}
