package com.agh.zlotowka.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

@Getter
public enum PeriodEnum {
    YEARLY(Period.ofYears(1)),
    MONTHLY(Period.ofMonths(1)),
    WEEKLY(Period.ofWeeks(1)),
    DAILY(Period.ofDays(1));

    private final Period period;

    PeriodEnum(Period period) {
        this.period = period;
    }

    public static PeriodEnum fromPeriod(Period period) {
        if (period == null) {
            throw new IllegalArgumentException("Period cannot be null");
        }

        return Arrays.stream(values())
                .filter(periodEnum -> periodEnum.getPeriod().equals(period))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching PeriodEnum for: " + period));
    }


    public LocalDate addToDate(LocalDate date) {
        return date.plus(period);
    }
}
