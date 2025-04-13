package com.agh.zlotowka.service;

import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.PeriodEnum;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserService userService;

    public void addOneTimeTransactionToUserBudget(List<OneTimeTransaction> oneTimeTransactionsToAdd) {
        log.info("Adding oneTimeTransactions to user budget...");
        for (OneTimeTransaction transaction : oneTimeTransactionsToAdd) {
            userService.addTransactionAmountToBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
    }

    @Transactional
    public void updateDataWithNewTransaction(RecurringTransaction recurringTransaction) {
        log.info("Updating data with new transaction from recurring...");
        updateRecurringTransaction(recurringTransaction);
        addOneTimeTransactionFromRecurring(recurringTransaction);
    }

    @Transactional
    public void addOneTimeTransactionFromRecurring(RecurringTransaction recurringTransaction) {
        log.info("Adding oneTimeTransaction from recurring...");
        OneTimeTransaction oneTimeTransaction = OneTimeTransaction.builder()
                .user(recurringTransaction.getUser())
                .name(recurringTransaction.getName())
                .amount(recurringTransaction.getAmount())
                .currency(recurringTransaction.getCurrency())
                .isIncome(recurringTransaction.getIsIncome())
                .date(LocalDate.now())
                .description(recurringTransaction.getDescription())
                .build();
        oneTimeTransactionRepository.save(oneTimeTransaction);

        userService.addTransactionAmountToBudget(
                recurringTransaction.getCurrency().getCurrencyId(),
                recurringTransaction.getAmount(),
                recurringTransaction.getIsIncome(),
                recurringTransaction.getUser());
    }

    @Transactional
    public void updateRecurringTransaction(RecurringTransaction recurringTransaction) {
        log.info("Updating recurring transaction...");
        PeriodEnum interval = recurringTransaction.getInterval();
        LocalDate newPaymentDate = interval.addToDate(recurringTransaction.getNextPaymentDate(), recurringTransaction.getFirstPaymentDate());

        if (!newPaymentDate.isAfter(recurringTransaction.getFinalPaymentDate()))
            recurringTransaction.setNextPaymentDate(newPaymentDate);
        recurringTransactionRepository.save(recurringTransaction);
    }
}
