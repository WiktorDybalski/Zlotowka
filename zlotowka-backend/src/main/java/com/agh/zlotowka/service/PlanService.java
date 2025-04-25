package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.PlanDTO;
import com.agh.zlotowka.dto.PlanRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        plan = planRepository.save(plan);
        log.info("Created new plan successfully");
        
        return getPlanDTO(plan);
    }

    public PlanDTO getPlan(Integer Id) {
        return getPlanDTO(planRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", Id))));
    }

    @Transactional
    public PlanDTO updatePlan(PlanRequest request, int planId){
        log.info("Updating plan with request: {}", request);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", planId)));

        validatePlanOwnership(request.userId(), plan.getUser().getUserId());

        return updatePlan2(request, plan);

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
        BigDecimal currentAmount = plan.getUser().getCurrentBudget();
        return PlanDTO.builder()
                .planId(plan.getPlanId())
                .userId(plan.getUser().getUserId())
                .name(plan.getName())
                .amount(plan.getRequiredAmount())
                .currencyId(plan.getCurrency().getCurrencyId())
                .description(plan.getDescription())
                .completed(plan.getCompleted())
                .subplansCompleted(plan.getSubplansCompleted())
                .canBeCompleted(currentAmount.compareTo(plan.getUser().getCurrentBudget()) <= 0)
                .actualAmount(currentAmount)
                .build();
    }

    private BigDecimal calculateCurrentBudget(Plan plan) {
        if (plan.getCompleted()) return plan.getRequiredAmount();
        BigDecimal currentBudget = plan.getUser().getCurrentBudget();
        //todo add sub-plans completed amounts
        return currentBudget;
    }

    @Transactional
    public void deletePlan(Integer id) {
        log.info("Deleting plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        planRepository.delete(plan);
        //todo remove sub-plans
    }

    @Transactional
    public PlanDTO completePlan(Integer id) {
        log.info("Completing plan with id: {}", id);
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Plan with Id %d not found", id)));

        plan.setCompleted(true);
        plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().subtract(calculateCurrentBudget(plan)));
        planRepository.save(plan);
        userRepository.save(plan.getUser());
        return getPlanDTO(plan);
    }

    private void validatePlanOwnership(Integer requestSenderId, Integer planOwner) {
        if (!requestSenderId.equals(planOwner)) {
            throw new IllegalArgumentException(String.format("User Id %d does not match the plan onwer", requestSenderId));
        }
    }

    private PlanDTO updatePlan2(PlanRequest request, Plan plan) {
        if (!Objects.equals(request.currencyId(), plan.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

            plan.setCurrency(currency);
        }
        //todo check sub-plans
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setRequiredAmount(request.amount());
//        plan.setCompleted(request.completed());

        planRepository.save(plan);
        return getPlanDTO(plan);
    }
}
