package com.agh.zlotowka.service;

import com.agh.zlotowka.model.PeriodEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PeriodService {
    public List<Map<String, String>> getPeriods() {
        return Arrays.stream(PeriodEnum.values())
                .map(period -> Map.of(
                        "name", getPolishName(period),
                        "code", period.getPeriod() != null ? period.getPeriod().toString() : "No period"
                ))
                .collect(Collectors.toList());
    }

    private String getPolishName(PeriodEnum period) {
        return switch (period) {
            case DAILY -> "Codziennie";
            case WEEKLY -> "Co tydzień";
            case MONTHLY -> "Co miesiąc";
            case YEARLY -> "Co rok";
            case ONCE -> "Raz";
        };
    }
}
