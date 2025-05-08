package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.GeneralPlanDTO;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.PlanType;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralPlansService {
    private final PlanRepository planRepository;
    private final SubPlanRepository subPlanRepository;

    public List<GeneralPlanDTO> getAllUncompletedPlans(Integer userId) {
        List<Plan> plans = planRepository.findAllUncompletedByUser(userId);
        List<Subplan> subplans = subPlanRepository.findAllUncompletedSubPlansByUserId(userId);

        return Stream.concat(
                plans.stream().map(this::getGeneralPlanDTO),
                subplans.stream().map(this::getGeneralPlanDTO)
        )
                .sorted(Comparator.comparing(GeneralPlanDTO::requiredAmount))
                .collect(Collectors.toList());
    }

    private GeneralPlanDTO getGeneralPlanDTO(Plan plan) {
        return new GeneralPlanDTO(
                plan.getRequiredAmount(),
                plan.getName(),
                plan.getDescription(),
                plan.getCurrency(),
                PlanType.PLAN
        );
    }

    private GeneralPlanDTO getGeneralPlanDTO(Subplan subplan) {
        return new GeneralPlanDTO(
                subplan.getRequiredAmount(),
                subplan.getName(),
                subplan.getDescription(),
                subplan.getPlan().getCurrency(),
                PlanType.SUBPLAN
        );
    }
}
