package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.PeriodDTO;
import com.agh.zlotowka.model.PeriodEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeriodService {
    public List<PeriodDTO> getPeriods() {
        return Arrays.stream(PeriodEnum.values())
                .map(period -> new PeriodDTO(
                        getPolishName(period),
                        period.getPeriod() != null ? period.getPeriod().toString() : "No period"
                ))
                .collect(Collectors.toList());
    }

    public PeriodDTO getPeriod(PeriodEnum periodEnum) {
        return new PeriodDTO(
            getPolishName(periodEnum),
            periodEnum.getPeriod() != null ? periodEnum.getPeriod().toString() : "No period");
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
