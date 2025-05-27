package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.PlanDTO;
import com.agh.zlotowka.dto.PlanRequest;
import com.agh.zlotowka.exception.*;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.*;
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
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final GeneralPlansService generalPlansService;

    @Transactional
    public PlanDTO createPlan(PlanRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));
        
        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow( () -> new EntityNotFoundException(String.format("Nie znaleziono waluty o ID %d", request.currencyId())));
        
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

        return getPlanDTO(plan);
    }

    public PlanDTO getPlan(Integer Id) {
        return getPlanDTO(planRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", Id))));
    }

    @Transactional
    public PlanDTO updatePlan(PlanRequest request, Integer planId){
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", planId)));

        validatePlanOwnership(request.userId(), plan.getUser().getUserId());
        validateCompletedPlanModification(request, plan);
        validateSubPlanAmounts(request, plan);

        return updatePlanLogic(request, plan);
    }

    public List<PlanDTO> getAllPlansByUserId(Integer userId) {
        List<Plan> plans = planRepository.findAllByUser(userId);

        if (plans.isEmpty())
            userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", userId)));

        return plans.stream()
                .map(this::getPlanDTO)
                .collect(Collectors.toList());
    }


    private PlanDTO getPlanDTO(Plan plan) {
        BigDecimal currentAmount = calculateCurrentBudget(plan);
        LocalDate estimatedCompletionDate = generalPlansService.estimateCompletionDate(
                plan,
                plan.getRequiredAmount().subtract(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId()))
        );

        return new PlanDTO(
                plan.getPlanId(),
                plan.getUser().getUserId(),
                plan.getName(),
                plan.getDescription(),
                plan.getRequiredAmount(),
                plan.getDate(),
                plan.getCurrency(),
                plan.getCompleted(),
                currentAmount,
                currentAmount.compareTo(plan.getRequiredAmount()) >= 0,
                plan.getSubplansCompleted(),
                estimatedCompletionDate
        );
    }


    @Transactional
    public void deletePlan(Integer id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", id)));

        List<Subplan> subPlans = subPlanRepository.findAllSubPlansByPlanId(id);
        subPlanRepository.deleteAll(subPlans);

        planRepository.delete(plan);
    }

    @Transactional
    public PlanDTO undoCompletePlan(Integer id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Marzenie o Id %d nie zostało znalezione", id)));

        validateIncompletePlan(plan);

        plan.setCompleted(false);
        plan.setDate(null);
        planRepository.save(plan);
        return getPlanDTO(plan);
    }

    private void validateIncompletePlan(Plan plan) {
        if (!plan.getCompleted())
            throw new PlanCompletionException("Marzenie nie jest zrealizowane, nie można cofnąć realizacji");
    }

    @Transactional
    public PlanDTO completePlan(Integer id, LocalDate completionDate) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", id)));

        validatePlanCompletion(plan);
        validateBudgetSufficiency(plan);
        validateCompletionDate(completionDate);

        try {
            BigDecimal correctedAmount = currencyService.convertCurrency(
                    plan.getRequiredAmount().subtract(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId())),
                    plan.getCurrency().getIsoCode(),
                    plan.getUser().getCurrency().getIsoCode()
            );

            plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().subtract(correctedAmount));
        } catch (CurrencyConversionException e) {
            log.error("Nieoczekiwany błąd w CurrencyService", e);
        }

        if (completionDate == null)
            completionDate = LocalDate.now();

        plan.setCompleted(true);
        plan.setDate(completionDate);
        plan.setSubplansCompleted(100.0);

        planRepository.save(plan);
        userRepository.save(plan.getUser());

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(plan.getUser())
                .name("Marzenie: " + plan.getName())
                .amount(plan.getRequiredAmount().subtract(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId())))
                .currency(plan.getCurrency())
                .isIncome(false)
                .date(plan.getDate())
                .description(plan.getDescription())
                .build();

        completeSubPlans(plan);
        oneTimeTransactionRepository.save(transaction);
        return getPlanDTO(plan);
    }

    private void validateCompletionDate(LocalDate completionDate) {
        if (completionDate != null && completionDate.isAfter(LocalDate.now())) {
            throw new PlanCompletionException("Data ukończenia nie może być w przyszłości");
        }
    }

    private void validatePlanCompletion(Plan plan) {
        if (plan.getCompleted())
            throw new PlanCompletionException("Plan jest już ukończony");
    }

    private void validateBudgetSufficiency(Plan plan) {
        BigDecimal currentAmount = calculateCurrentBudget(plan);
        if (currentAmount.compareTo(plan.getRequiredAmount()) < 0)
            throw new BudgetInsufficientException("Plan nie może zostać ukończony, nie osiągnięto wymaganej kwoty");
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
            throw new PlanOwnershipException(String.format("ID użytkownika %d nie odpowiada właścicielowi planu", requestSenderId));
        }
    }

    private PlanDTO updatePlanLogic(PlanRequest request, Plan plan) {
        if(!request.currencyId().equals(plan.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono waluty o ID %d", request.currencyId())));
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
                throw new PlanCompletionException("Nie można zmienić kwoty lub waluty ukończonego planu");
            }
        }
    }

    private void validateSubPlanAmounts(PlanRequest request, Plan plan) {
        BigDecimal allSubPlansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());
        if (allSubPlansAmount.compareTo(request.amount()) > 0) {
            throw new PlanAmountExceededException("Łączna kwota podplanów przekracza wymaganą kwotę planu");
        }
    }

    private BigDecimal calculateCurrentBudget(Plan plan) {
        if (plan.getCompleted()) return plan.getRequiredAmount();

        try {
            BigDecimal currentBudget = currencyService.convertCurrency(
                    plan.getUser().getCurrentBudget(),
                    plan.getUser().getCurrency().getIsoCode(),
                    plan.getCurrency().getIsoCode()
            );

            return currentBudget.add(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId()));
        }
        catch (CurrencyConversionException e) {
            log.error("Nieoczekiwany błąd w CurrencyService", e);
        }
        return BigDecimal.ZERO;
    }
}
