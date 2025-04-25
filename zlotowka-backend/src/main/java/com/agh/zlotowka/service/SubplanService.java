package com.agh.zlotowka.service;


import com.agh.zlotowka.dto.SubplanDTO;
import com.agh.zlotowka.dto.SubplanRequest;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubplanService {
    private final PlanRepository planRepository;
    private final SubPlanRepository subPlanRepository;

    @Transactional
    public SubplanDTO createSubplan(SubplanRequest request) {
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", request.planId())));

        validateSubplanAmount(plan, request.amount());

        Subplan subplan = Subplan.builder()
                .plan(plan)
                .name(request.name())
                .requiredAmount(request.amount())
                .description(request.description())
                .completed(false)
                .build();

        subplan = subPlanRepository.save(subplan);
        log.info("Created new subplan successfully");

        return getSubplanDTO(subplan);
    }

    private SubplanDTO getSubplanDTO(Subplan subplan) {
        return SubplanDTO.builder()
                .planId(subplan.getPlan().getPlanId())
                .name(subplan.getName())
                .amount(subplan.getRequiredAmount())
                .description(subplan.getDescription())
                .completed(false)
                .build();
    }

    public SubplanDTO getSubplan(Integer id) {
        return getSubplanDTO(subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subplan with Id %d not found", id))));
    }

    @Transactional
    public SubplanDTO updateSubplan(SubplanRequest request, Integer subplanId) {
        log.info("Updating subplan with request: {}", request);
        Subplan subplan = subPlanRepository.findById(subplanId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subplan with Id %d not found", subplanId)));

        validateSubplanAmount(subplan.getPlan(), request.amount());

        subplan.setName(request.name());
        subplan.setRequiredAmount(request.amount());

        subplan.setDescription(request.description());
        subplan.setRequiredAmount(request.amount());
        subplan.setName(request.name());

        subPlanRepository.save(subplan);
        return getSubplanDTO(subplan);
    }

    private void validateSubplanAmount(Plan plan, BigDecimal newAmount) {
        BigDecimal allSubplansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());
        if (allSubplansAmount.add(newAmount).compareTo(plan.getRequiredAmount()) >= 0) {
            throw new IllegalArgumentException("Total subplan amount exceeds the plan's required amount");
        }
    }

    public SubplanDTO completeSubplan(Integer id) {
        log.info("Completing subplan with id: {}", id);
        Subplan subplan = subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subplan with Id %d not found", id)));

        subplan.setCompleted(true);
        subplan.setDate(LocalDate.now());

        Plan plan = subplan.getPlan();
        Integer totalSubplans = subPlanRepository.getSubplanCount(plan.getPlanId());
        Integer completedSubplans = subPlanRepository.getCompletedSubPlanCount(plan.getPlanId());
        plan.setSubplansCompleted((double) completedSubplans/totalSubplans);


        planRepository.save(plan);
        subPlanRepository.save(subplan);

        return getSubplanDTO(subplan);
    }

    public List<SubplanDTO> getAllSubplansByPlanId(Integer planId) {
        List<Subplan> subplans = subPlanRepository.findAllSubPlanByPlanId(planId);

        if (subplans.isEmpty()) {
            planRepository.findById(planId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", planId)));
        }

        return subplans.stream()
                .map(this::getSubplanDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubplan(Integer id) {
        log.info("Deleting subplan with id: {}", id);
        Subplan subplan = subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subplan with Id %d not found", id)));

        if (subplan.getCompleted()) {
            subplan.getPlan().getUser().setCurrentBudget(
                    subplan.getPlan().getUser().getCurrentBudget().add(subplan.getRequiredAmount()));
        }

        subPlanRepository.delete(subplan);
    }
}
