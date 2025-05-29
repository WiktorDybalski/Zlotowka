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
    DAILY(Period.ofDays(1)),
    ONCE(null);

    private final Period period;

    PeriodEnum(Period period) {
        this.period = period;
    }

    public static PeriodEnum fromPeriod(Period period) {
        if (period == null) {
            throw new IllegalArgumentException("Okres nie może być pusty");
        }

        return Arrays.stream(values())
                .filter(periodEnum -> periodEnum.getPeriod().equals(period))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nie znaleziono pasującego PeriodEnum dla: " + period));
    }


    public LocalDate addToDate(LocalDate previousDate, LocalDate startDate) {
        if (this == ONCE) {
            return previousDate;
        }

        LocalDate newDate = previousDate.plus(period);

        if (this == MONTHLY && newDate.getDayOfMonth() < startDate.getDayOfMonth()) {
            newDate = newDate.withDayOfMonth(Math.min(newDate.lengthOfMonth(), startDate.getDayOfMonth()));
        }

        return newDate;
    }
}
