package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.PlanType;

import java.math.BigDecimal;

public record GeneralPlanDTO(
        Integer id,
        BigDecimal requiredAmount,
        String name,
        PlanType planType
) {
}
