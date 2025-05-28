package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.GeneralPlanDTO;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.PlanType;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralPlansService {
    private final PlanRepository planRepository;
    private final SubPlanRepository subPlanRepository;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public List<GeneralPlanDTO> getAllUncompletedPlans(Integer userId) {
        String userCurrencyCode = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", userId)))
                .getCurrency()
                .getIsoCode();

        List<Plan> plans = planRepository.findAllUncompletedByUser(userId);

        List<GeneralPlanDTO> result = new ArrayList<>();

        for (Plan plan : plans) {
            String planCurrencyCode = plan.getCurrency().getIsoCode();

            result.add(createPlanDTO(
                    plan.getPlanId(),
                    plan.getRequiredAmount().subtract(subPlanRepository.getTotalSubPlanAmountCompleted(plan.getPlanId())),
                    plan.getName(),
                    planCurrencyCode,
                    userCurrencyCode,
                    PlanType.PLAN)
            );

            List<Subplan> subplans = subPlanRepository.findAllUncompletedSubPlansByPlanId(plan.getPlanId());

            for (Subplan subplan : subplans)
                result.add(createPlanDTO(
                        subplan.getSubplanId(),
                        subplan.getRequiredAmount(),
                        subplan.getName(),
                        planCurrencyCode,
                        userCurrencyCode,
                        PlanType.SUBPLAN)
                );
        }

        return result.stream()
                .sorted(Comparator.comparing(GeneralPlanDTO::requiredAmount))
                .toList();
    }

    private GeneralPlanDTO createPlanDTO(
            Integer id,
            BigDecimal amount,
            String name,
            String planCurrencyCode,
            String userCurrencyCode,
            PlanType planType
    ) {
        BigDecimal convertedAmount = convertAmount(amount, planCurrencyCode, userCurrencyCode);

        return new GeneralPlanDTO(
                id,
                convertedAmount,
                name,
                planType
        );
    }

    private BigDecimal convertAmount(BigDecimal amount, String sourceCurrencyCode, String targetCurrencyCode) {
        if (sourceCurrencyCode.equals(targetCurrencyCode)) {
            return amount;
        }
        BigDecimal convertedAmount = BigDecimal.ZERO;
        try {
            convertedAmount = currencyService.convertCurrency(amount, sourceCurrencyCode, targetCurrencyCode);
        } catch (CurrencyConversionException e) {
            log.error("Nieoczekiwany błąd w CurrencyService", e);
        }
        return convertedAmount;
    }

    protected LocalDate estimateCompletionDate(Plan plan, BigDecimal planAmount) {
        BigDecimal budget = plan.getUser().getCurrentBudget();
        Currency userCurrency = plan.getUser().getCurrency();
        Currency planCurrency = plan.getCurrency();
        LocalDate date = LocalDate.now();
        LocalDate finalDate = LocalDate.now().plusYears(5);
        Integer userId = plan.getUser().getUserId();

        List<OneTimeTransaction> allTransactions = oneTimeTransactionRepository.getTransactionsInRange(userId, date, finalDate);
        List<OneTimeTransaction> transactions = new ArrayList<>(allTransactions.stream()
                .sorted(Comparator.comparing(OneTimeTransaction::getDate))
                .toList());

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.getActiveTransactionsByUser(userId, date, finalDate);
        try {
            planAmount = currencyService.convertCurrency(planAmount, planCurrency.getIsoCode(), userCurrency.getIsoCode());
            while (date.isBefore(finalDate)) {
                Iterator<OneTimeTransaction> iterator = transactions.iterator();
                while (iterator.hasNext()) {
                    OneTimeTransaction transaction = iterator.next();
                    if (transaction.getDate().equals(date)) {
                        BigDecimal amount = currencyService.convertCurrency(
                                transaction.getAmount(),
                                transaction.getCurrency().getIsoCode(),
                                userCurrency.getIsoCode()
                        );

                        budget = transaction.getIsIncome() ?
                                budget.add(amount) :
                                budget.subtract(amount);

                        iterator.remove();
                    }
                    else if (transaction.getDate().isAfter(date)) {
                        break;
                    }
                }
                for (RecurringTransaction transaction : recurringTransactions) {
                    if (transaction.getFinalPaymentDate().isBefore(date)) continue;

                    long days = ChronoUnit.DAYS.between(transaction.getNextPaymentDate(), date);
                    if (days < 0) continue;

                    if (days % transaction.getInterval().getPeriod().getDays() == 0) {
                        BigDecimal amount = currencyService.convertCurrency(
                                transaction.getAmount(),
                                transaction.getCurrency().getIsoCode(),
                                userCurrency.getIsoCode()
                        );

                        budget = transaction.getIsIncome() ?
                                budget.add(amount) :
                                budget.subtract(amount);
                    }
                }

                if (budget.compareTo(planAmount) >= 0) {
                    return date;
                }

                date = date.plusDays(1);
            }
        }
        catch (CurrencyConversionException e) {
            log.error("Nieoczekiwany błąd w CurrencyService", e);
        }
        return null;
    }
}