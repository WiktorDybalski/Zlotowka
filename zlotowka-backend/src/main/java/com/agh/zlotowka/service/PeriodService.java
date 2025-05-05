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
                        "code", period.getPeriod().toString()
                ))
                .collect(Collectors.toList());
    }

    private String getPolishName(PeriodEnum period) {
        return switch (period) {
            case DAILY -> "Dzienny";
            case WEEKLY -> "Tygodniowy";
            case MONTHLY -> "Miesięczny";
            case YEARLY -> "Roczny";
            case ONCE -> "Jednorazowy";
        };
    }
}
