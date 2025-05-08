package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.PlanType;

import java.math.BigDecimal;
import java.util.List;

public record GeneralPlanDTO(
        BigDecimal requiredAmount,
        String name,
        PlanType planType,
        List<GeneralPlanDTO> subplans
) {
}
