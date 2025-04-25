package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.PlanDTO;
import com.agh.zlotowka.dto.PlanRequest;
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
import java.util.Objects;
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

    private BigDecimal calculateCurrentBudget(Plan plan) {
        if (plan.getCompleted()) return plan.getRequiredAmount();
        BigDecimal currentBudget = plan.getUser().getCurrentBudget();
        return currentBudget.add(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId()));
    }

    private BigDecimal calculateRequiredAmount(Plan plan) {
        return plan.getRequiredAmount().subtract(subPlanRepository.getTotalSubPlanAmount(plan.getPlanId()));
    }

    @Transactional
    public void deletePlan(Integer id) {
        log.info("Deleting plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        List<Subplan> subPlans = subPlanRepository.findAllSubPlanByPlanId(id);
        for (Subplan subPlan : subPlans) {
            if (subPlan.getCompleted()) {
                plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().add(subPlan.getRequiredAmount()));
                userRepository.save(plan.getUser());
            }
            subPlanRepository.delete(subPlan);
        }
        planRepository.delete(plan);
    }

    @Transactional
    public PlanDTO completePlan(Integer id) {
        log.info("Completing plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        if (plan.getCompleted())
            throw new IllegalArgumentException("Plan is already completed");

        BigDecimal currentAmount = calculateCurrentBudget(plan);
        if (currentAmount.compareTo(plan.getRequiredAmount()) < 0)
            throw new IllegalArgumentException("Plan cannot be completed, required amount not reached");

        plan.setCompleted(true);
        plan.setDate(LocalDate.now());
        plan.getUser().setCurrentBudget(calculateRequiredAmount(plan));
        planRepository.save(plan);
        userRepository.save(plan.getUser());
        return getPlanDTO(plan);
    }

    private void validatePlanOwnership(Integer requestSenderId, Integer planOwner) {
        if (!requestSenderId.equals(planOwner)) {
            throw new IllegalArgumentException(String.format("User Id %d does not match the plan onwer", requestSenderId));
        }
    }

    private PlanDTO updatePlanLogic(PlanRequest request, Plan plan) {
        if (!Objects.equals(request.currencyId(), plan.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

            plan.setCurrency(currency);
        }
        Integer totalCompletedSubplans = subPlanRepository.getCompletedSubPlanCount(plan.getPlanId());
        if (totalCompletedSubplans > 0 && !request.currencyId().equals(plan.getCurrency().getCurrencyId())) {
            throw new IllegalArgumentException("Cannot change currency of plan with completed subplans");
        }
        BigDecimal allSubPlansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());

        if (allSubPlansAmount.compareTo(request.amount()) > 0) {
            throw new IllegalArgumentException("Total subplan amount exceeds the plan's required amount");
        }

        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setRequiredAmount(request.amount());

        planRepository.save(plan);
        return getPlanDTO(plan);
    }
}
