package com.agh.zlotowka.service;


import com.agh.zlotowka.dto.SubplanDTO;
import com.agh.zlotowka.dto.SubplanRequest;
import com.agh.zlotowka.exception.*;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
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
    private final OneTimeTransactionRepository oneTimeTransactionRepository;

    public void validateOwnershipBySubplanId(Integer subplanId, CustomUserDetails userDetails) {
        Subplan subplan = subPlanRepository.findById(subplanId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono podplanu o ID " + subplanId));
        Integer ownerId = subplan.getPlan().getUser().getUserId();
        if (!ownerId.equals(userDetails.getUser().getUserId())) {
            throw new PlanOwnershipException("Dostęp zabroniony do tego podplanu");
        }
    }

    public void validateOwnershipByPlanId(Integer planId, CustomUserDetails userDetails) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono planu o ID " + planId));
        Integer ownerId = plan.getUser().getUserId();
        if (!ownerId.equals(userDetails.getUser().getUserId())) {
            throw new PlanOwnershipException("Dostęp zabroniony do tego planu");
        }
    }

    @Transactional
    public SubplanDTO createSubplan(SubplanRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", request.planId())));

        validateSubplanOwnership(request.userId(), plan.getUser().getUserId());
        validateSubplanAmount(plan, request.amount());
        validatePlanCompletion(plan);


        Subplan subplan = Subplan.builder()
                .plan(plan)
                .name(request.name())
                .requiredAmount(request.amount())
                .description(request.description())
                .completed(false)
                .build();

        subplan = subPlanRepository.save(subplan);
        calculatePlanSubplanCompletion(subplan.getPlan());

        return getSubplanDTO(subplan);
    }

    private SubplanDTO getSubplanDTO(Subplan subplan) {
        BigDecimal actualAmount = calculateCurrentBudget(subplan);
        boolean canBeCompleted = actualAmount.compareTo(subplan.getRequiredAmount()) >= 0;

        return new SubplanDTO(
                subplan.getPlan().getPlanId(),
                subplan.getSubplanId(),
                subplan.getName(),
                subplan.getDescription(),
                subplan.getRequiredAmount(),
                subplan.getPlan().getCurrency(),
                subplan.getCompleted(),
                actualAmount,
                canBeCompleted,
                subplan.getDate()
        );
    }

    public SubplanDTO getSubplan(Integer id) {
        return getSubplanDTO(subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono podplanu o ID %d", id))));
    }

    @Transactional
    public SubplanDTO updateSubplan(SubplanRequest request, Integer subplanId) {

        userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));

        Subplan subplan = subPlanRepository.findById(subplanId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono podplanu o ID %d", subplanId)));

        validateCompletedSubPlanModification(request, subplan);
        validateSubplanOwnership(request.userId(), subplan.getPlan().getUser().getUserId());
        validateUpdatedSubplanAmount(subplan.getPlan(), request.amount(), subplan);


        subplan.setName(request.name());
        subplan.setRequiredAmount(request.amount());

        subplan.setDescription(request.description());
        subplan.setRequiredAmount(request.amount());
        subplan.setName(request.name());

        subPlanRepository.save(subplan);
        return getSubplanDTO(subplan);
    }

    private void validateCompletedSubPlanModification(SubplanRequest request, Subplan subplan) {
        if (subplan.getCompleted() && !request.amount().equals(subplan.getRequiredAmount())) {
            throw new PlanCompletionException("Podplan jest już ukończony, nie można zmienić kwoty");
        }
    }

    private void validatePlanCompletion(Plan plan) {
        if (plan.getCompleted()) {
            throw new PlanCompletionException("Plan jest już ukończony, nie można dodać podplanu");
        }
    }

    private void validateSubplanOwnership(Integer userId, Integer subplanUserId) {
        if (!userId.equals(subplanUserId)) {
            throw new PlanOwnershipException(String.format("ID użytkownika %d nie odpowiada właścicielowi podplanu", userId));
        }
    }

    private void validateSubplanAmount(Plan plan, BigDecimal newAmount) {
        BigDecimal allSubplansAmount = subPlanRepository.getTotalSubPlanAmount(plan.getPlanId());
        if (allSubplansAmount.add(newAmount).compareTo(plan.getRequiredAmount()) > 0) {
            throw new PlanAmountExceededException("Łączna kwota podplanów przekracza wymaganą kwotę planu");
        }
    }

    private void validateUpdatedSubplanAmount(Plan plan, BigDecimal newAmount, Subplan subplan) {
        BigDecimal allSubplansAmountMinusOld =
                subPlanRepository.getTotalSubPlanAmount(plan.getPlanId()).subtract(subplan.getRequiredAmount());
        if (allSubplansAmountMinusOld.add(newAmount).compareTo(plan.getRequiredAmount()) > 0) {
            throw new PlanAmountExceededException("Łączna kwota podplanów przekracza wymaganą kwotę planu");
        }
    }

    @Transactional
    public SubplanDTO completeSubplan(Integer id, LocalDate completionDate) {
        Subplan subplan = subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono podplanu o ID %d", id)));

        validateSubPlanCompletion(subplan);
        validateSubPlanBudgetSufficiency(subplan);
        validateCompletionDate(completionDate);

        Plan plan = subplan.getPlan();

        try {
            BigDecimal correctAmount = currencyService.convertCurrency(
                    subplan.getRequiredAmount(),
                    subplan.getPlan().getCurrency().getIsoCode(),
                    subplan.getPlan().getUser().getCurrency().getIsoCode()
            );

            plan.getUser().setCurrentBudget(plan.getUser().getCurrentBudget().subtract(correctAmount));
        }
        catch (CurrencyConversionException e) {
            log.error("Nieoczekiwany błąd z CurrencyService", e);
        }

        if (completionDate == null)
            completionDate = LocalDate.now();

        subplan.setCompleted(true);
        subplan.setDate(completionDate);

        calculatePlanSubplanCompletion(plan);

        userRepository.save(plan.getUser());
        subPlanRepository.save(subplan);

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(plan.getUser())
                .name("Część marzenia: " + subplan.getName())
                .amount(subplan.getRequiredAmount())
                .currency(plan.getCurrency())
                .isIncome(false)
                .date(subplan.getDate())
                .description(subplan.getDescription())
                .build();

        oneTimeTransactionRepository.save(transaction);
        return getSubplanDTO(subplan);
    }

    private void validateCompletionDate(LocalDate completionDate) {
        if (completionDate != null && completionDate.isAfter(LocalDate.now())) {
            throw new PlanCompletionException("Data ukończenia nie może być w przyszłości");
        }
    }

    private void validateSubPlanBudgetSufficiency(Subplan subplan) {
        BigDecimal currentAmount = BigDecimal.ZERO;
        try {
            currentAmount = currencyService.convertCurrency(
                    subplan.getPlan().getUser().getCurrentBudget(),
                    subplan.getPlan().getUser().getCurrency().getIsoCode(),
                    subplan.getPlan().getCurrency().getIsoCode()
            );
        }
        catch (CurrencyConversionException e) {
            log.error("Unexpected error from CurrencyService", e);
        }
        if (currentAmount.compareTo(subplan.getRequiredAmount()) < 0) {
            throw new InsufficientBudgetException("Podplan nie może zostać ukończony, nie osiągnięto wymaganej kwoty");
        }
    }

    private void validateSubPlanCompletion(Subplan subplan) {
        if (subplan.getCompleted()) {
            throw new PlanCompletionException("Podplan jest już ukończony");
        }
    }

    public List<SubplanDTO> getAllSubplansByPlanId(Integer planId) {
        List<Subplan> subplans = subPlanRepository.findAllSubPlansByPlanId(planId);

        if (subplans.isEmpty()) {
            planRepository.findById(planId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono planu o ID %d", planId)));
        }

        return subplans.stream()
                .map(this::getSubplanDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubplan(Integer id) {
        Subplan subplan = subPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono podplanu o ID %d", id)));

        subPlanRepository.delete(subplan);
        calculatePlanSubplanCompletion(subplan.getPlan());
    }

    void calculatePlanSubplanCompletion(Plan plan) {
        Integer totalSubplans = subPlanRepository.getSubplanCount(plan.getPlanId());
        Integer completedSubplans = subPlanRepository.getCompletedSubPlanCount(plan.getPlanId());
        plan.setSubplansCompleted((double) completedSubplans / totalSubplans * 100);
        planRepository.save(plan);
    }

    private BigDecimal calculateCurrentBudget(Subplan subplan) {
        BigDecimal currentBudget = BigDecimal.ZERO;
        if (subplan.getCompleted()) {
            currentBudget = subplan.getRequiredAmount();
        }
        else {
            try {
                currentBudget = currencyService.convertCurrency(
                        subplan.getPlan().getUser().getCurrentBudget(),
                        subplan.getPlan().getUser().getCurrency().getIsoCode(),
                        subplan.getPlan().getCurrency().getIsoCode()
                );
            } catch (CurrencyConversionException e) {
                log.error("Unexpected error from CurrencyService", e);
            }
        }
        return currentBudget;
    }
}
