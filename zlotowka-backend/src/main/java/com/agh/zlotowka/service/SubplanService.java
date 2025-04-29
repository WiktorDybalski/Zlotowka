package com.agh.zlotowka.service;


import com.agh.zlotowka.dto.SubplanDTO;
import com.agh.zlotowka.dto.SubplanRequest;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import com.agh.zlotowka.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CurrencyService currencyService;

    @Transactional
    public SubplanDTO createSubplan(SubplanRequest request) {
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", request.planId())));

        validateSubplanOwnership(request.userId(), plan.getUser().getUserId());
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
                .completed(subplan.getCompleted())
                .subplanId(subplan.getSubplanId())
                .currencyId(subplan.getPlan().getCurrency().getCurrencyId())
                .actualAmount(calculateCurrentBudget(subplan))
                .canBeCompleted(subplan.getRequiredAmount().compareTo(subplan.getPlan().getUser().getCurrentBudget()) <= 0)
                .date(subplan.getDate())
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

        validateCompletedSubPlanModifiaction(request, subplan);
        validateSubplanOwnership(request.userId(), subplan.getPlan().getUser().getUserId());
        validateSubplanAmount(subplan.getPlan(), request.amount());


        subplan.setName(request.name());
        subplan.setRequiredAmount(request.amount());

        subplan.setDescription(request.description());
        subplan.setRequiredAmount(request.amount());
        subplan.setName(request.name());

        subPlanRepository.save(subplan);
        return getSubplanDTO(subplan);
    }

    private void validateCompletedSubPlanModifiaction(SubplanRequest request, Subplan subplan) {
        if (subplan.getCompleted() && !request.amount().equals(subplan.getRequiredAmount())) {
            throw new IllegalArgumentException("Subplan is already completed, cannot change amount");
        }
    }

    private void validateSubplanOwnership(Integer userId, Integer subplanUserId) {
        if (!userId.equals(subplanUserId)) {
            throw new IllegalArgumentException(String.format("User Id %d does not match the subplan owner", userId));
        }
    }

    private void validateSubplanAmount(Plan plan, BigDecimal newAmount) {
        BigDecimal allSubplansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());
        if (allSubplansAmount.add(newAmount).compareTo(plan.getRequiredAmount()) > 0) {
            throw new IllegalArgumentException("Total subplan amount exceeds the plan's required amount");
        }
    }

    @Transactional
    public SubplanDTO completeSubplan(Integer id) {
        log.info("Completing subplan with id: {}", id);
        Subplan subplan = subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Subplan with Id %d not found", id)));

        validateSubPlanCompletion(subplan);
        validateSubPlanBudgetSufficiency(subplan);

        subplan.setCompleted(true);
        subplan.setDate(LocalDate.now());

        Plan plan = subplan.getPlan();
        calculatePlanSubplanCompletion(plan);
        BigDecimal correctAmount = calculateCorrectedAmount(
                subplan.getPlan().getCurrency().getIsoCode(),
                subplan.getPlan().getUser().getCurrency().getIsoCode(),
                subplan.getRequiredAmount()
        );

        plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().subtract(correctAmount));

        userRepository.save(plan.getUser());
        subPlanRepository.save(subplan);

        return getSubplanDTO(subplan);
    }

    private void validateSubPlanBudgetSufficiency(Subplan subplan) {
        BigDecimal currentAmount = calculateCorrectedAmount(
                subplan.getPlan().getUser().getCurrency().getIsoCode(),
                subplan.getPlan().getCurrency().getIsoCode(),
                subplan.getPlan().getUser().getCurrentBudget()
        );

        if (currentAmount.compareTo(subplan.getRequiredAmount()) < 0) {
            throw new IllegalArgumentException("Subplan cannot be completed, required amount not reached");
        }
    }

    private void validateSubPlanCompletion(Subplan subplan) {
        if (subplan.getCompleted()) {
            throw new IllegalArgumentException("Subplan is already completed");
        }
    }

    public List<SubplanDTO> getAllSubplansByPlanId(Integer planId) {
        List<Subplan> subplans = subPlanRepository.findAllSubPlansByPlanId(planId);

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

        User user = subplan.getPlan().getUser();
        if (subplan.getCompleted() && !subplan.getPlan().getCompleted()) {
            BigDecimal correctedAmount = calculateCorrectedAmount(
                    subplan.getPlan().getCurrency().getIsoCode(),
                    user.getCurrency().getIsoCode(),
                    subplan.getRequiredAmount()
            );
            user.setCurrentBudget(user.getCurrentBudget().add(correctedAmount));
        }

        userRepository.save(user);
        subPlanRepository.delete(subplan);
        calculatePlanSubplanCompletion(subplan.getPlan());
    }

    void calculatePlanSubplanCompletion(Plan plan) {
        Integer totalSubplans = subPlanRepository.getSubplanCount(plan.getPlanId());
        Integer completedSubplans = subPlanRepository.getCompletedSubPlanCount(plan.getPlanId());
        plan.setSubplansCompleted((double) completedSubplans / totalSubplans);
        planRepository.save(plan);
    }

    BigDecimal calculateCorrectedAmount(String fromCurrency, String toCurrency, BigDecimal amount) {
        try {
            return currencyService.convertCurrency(amount, fromCurrency, toCurrency);
        } catch (CurrencyConversionException e) {
            log.error("Currency conversion failed", e);
            throw new IllegalArgumentException("Currency conversion failed");
        } catch (Exception e) {
            log.error("Unexpected error from CurrencyService", e);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateCurrentBudget(Subplan subplan) {
        BigDecimal currentBudget;
        if (subplan.getCompleted()) {
            currentBudget = subplan.getRequiredAmount();
        }
        else {
            currentBudget = calculateCorrectedAmount(
                    subplan.getPlan().getUser().getCurrency().getIsoCode(),
                    subplan.getPlan().getCurrency().getIsoCode(),
                    subplan.getPlan().getUser().getCurrentBudget()
            );
        }
        return currentBudget;
    }
}
