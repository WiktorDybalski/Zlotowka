package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.PlanType;

import java.math.BigDecimal;

public record GeneralPlanDTO(
        BigDecimal requiredAmount,
        String name,
        PlanType planType
) {
}
