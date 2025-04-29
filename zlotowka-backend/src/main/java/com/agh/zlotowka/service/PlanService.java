package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.PlanDTO;
import com.agh.zlotowka.dto.PlanRequest;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.CurrencyRepository;
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
public class PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final SubPlanRepository subPlanRepository;
    private final CurrencyService currencyService;

    @Transactional
    public PlanDTO createPlan(PlanRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", request.userId())));
        
        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow( () -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));
        
        Plan plan = Plan.builder()
                .user(user)
                .name(request.name())
                .requiredAmount(request.amount())
                .currency(currency)
                .description(request.description())
                .completed(false)
                .subplansCompleted(0.0)
                .build();

        planRepository.save(plan);
        log.info("Created new plan successfully");
        
        return getPlanDTO(plan);
    }

    public PlanDTO getPlan(Integer Id) {
        return getPlanDTO(planRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", Id))));
    }

    @Transactional
    public PlanDTO updatePlan(PlanRequest request, Integer planId){
        log.info("Updating plan with request: {}", request);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", planId)));

        validatePlanOwnership(request.userId(), plan.getUser().getUserId());

        return updatePlanLogic(request, plan);
    }

    public List<PlanDTO> getAllPlansByUserId(Integer userId) {
        List<Plan> plans = planRepository.findAllByUser(userId);

        if (plans.isEmpty())
            userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        return plans.stream()
                .map(this::getPlanDTO)
                .collect(Collectors.toList());
    }
    
    
    private PlanDTO getPlanDTO(Plan plan) {
        BigDecimal currentAmount = calculateCurrentBudget(plan);

        return PlanDTO.builder()
                .planId(plan.getPlanId())
                .userId(plan.getUser().getUserId())
                .name(plan.getName())
                .date(plan.getDate())
                .amount(plan.getRequiredAmount())
                .currencyId(plan.getCurrency().getCurrencyId())
                .description(plan.getDescription())
                .completed(plan.getCompleted())
                .subplansCompleted(plan.getSubplansCompleted())
                .canBeCompleted(currentAmount.compareTo(plan.getRequiredAmount()) >= 0)
                .actualAmount(currentAmount)
                .build();
    }


    @Transactional
    public void deletePlan(Integer id) {
        log.info("Deleting plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        List<Subplan> subPlans = subPlanRepository.findAllSubPlansByPlanId(id);

        deleteSubPlans(subPlans, plan);
        planRepository.delete(plan);
    }

    private void deleteSubPlans(List<Subplan> subPlans, Plan plan) {
        User user = plan.getUser();

        for (Subplan subPlan : subPlans) {
            if (subPlan.getCompleted() && !plan.getCompleted()) {
                BigDecimal correctedAmount = calculateCorrectedAmount(
                        subPlan.getPlan().getCurrency().getIsoCode(),
                        user.getCurrency().getIsoCode(),
                        subPlan.getRequiredAmount()
                );

                user.setCurrentBudget(user.getCurrentBudget().add(correctedAmount));
                userRepository.save(plan.getUser());
            }
            subPlanRepository.delete(subPlan);
        }
    }


    @Transactional
    public PlanDTO completePlan(Integer id) {
        log.info("Completing plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        validatePlanCompletion(plan);
        validateBudgetSufficiency(plan);

        completeSubPlans(plan);

        plan.setCompleted(true);
        plan.setDate(LocalDate.now());
        BigDecimal correctedAmount = calculateCorrectedAmount(
                plan.getCurrency().getIsoCode(),
                plan.getUser().getCurrency().getIsoCode(),
                plan.getRequiredAmount()
        );
        plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().subtract(correctedAmount));

        planRepository.save(plan);
        userRepository.save(plan.getUser());

        return getPlanDTO(plan);
    }

    private void validatePlanCompletion(Plan plan) {
        if (plan.getCompleted())
            throw new IllegalArgumentException("Plan is already completed");
    }

    private void validateBudgetSufficiency(Plan plan) {
        BigDecimal currentAmount = calculateCurrentBudget(plan);
        if (currentAmount.compareTo(plan.getRequiredAmount()) < 0)
            throw new IllegalArgumentException("Plan cannot be completed, required amount not reached");
    }

    private void completeSubPlans(Plan plan) {
        List<Subplan> subPlans = subPlanRepository.findAllSubPlansByPlanId(plan.getPlanId());

        for (Subplan subPlan : subPlans) {
            if (!subPlan.getCompleted()) {
                subPlan.setCompleted(true);
                subPlan.setDate(plan.getDate());
                subPlanRepository.save(subPlan);
            }
        }
    }

    private void validatePlanOwnership(Integer requestSenderId, Integer planOwner) {
        if (!requestSenderId.equals(planOwner)) {
            throw new IllegalArgumentException(String.format("User Id %d does not match the plan onwer", requestSenderId));
        }
    }

    private PlanDTO updatePlanLogic(PlanRequest request, Plan plan) {
        validateCompletedPlanModification(request, plan);
        validateCurrencyConsistency(request, plan);
        validateSubPlanAmounts(request, plan);

        if(!request.currencyId().equals(plan.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));
            plan.setCurrency(currency);
        }

        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setRequiredAmount(request.amount());

        planRepository.save(plan);
        return getPlanDTO(plan);
    }

    private void validateCompletedPlanModification(PlanRequest request, Plan plan) {
        if(plan.getCompleted()) {
            boolean isAmountChanged = !request.amount().equals(plan.getRequiredAmount());
            boolean isCurrencyChanged = !request.currencyId().equals(plan.getCurrency().getCurrencyId());

            if (isAmountChanged || isCurrencyChanged) {
                throw new IllegalArgumentException("Cannot change amount or currency of completed plan");
            }
        }
    }

    private void validateSubPlanAmounts(PlanRequest request, Plan plan) {
        BigDecimal allSubPlansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());
        if (allSubPlansAmount.compareTo(request.amount()) > 0) {
            throw new IllegalArgumentException("Total subplan amount exceeds the plan's required amount");
        }
    }

    private void validateCurrencyConsistency(PlanRequest request, Plan plan) {
        Integer totalSubPlansCount = subPlanRepository.getSubplanCount(plan.getPlanId());
        if (totalSubPlansCount > 0 && !request.currencyId().equals(plan.getCurrency().getCurrencyId())) {
            throw new IllegalArgumentException("Cannot change currency of plan with subplans");
        }
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

    private BigDecimal calculateCurrentBudget(Plan plan) {
        if (plan.getCompleted()) return plan.getRequiredAmount();

        BigDecimal currentBudget = calculateCorrectedAmount(
                plan.getUser().getCurrency().getIsoCode(),
                plan.getCurrency().getIsoCode(),
                plan.getUser().getCurrentBudget()
        );

        return currentBudget.add(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId()));
    }
}
