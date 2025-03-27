package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;


    //    @Scheduled(cron = "0 0 19 * * ?") // Everyday at 19:00
    public void addRecurringTransactions() {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findDueRecurringTransactions();
        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            updateDataWithNewTransaction(recurringTransaction);
        }
    }

    @Transactional
    public void updateDataWithNewTransaction(RecurringTransaction recurringTransaction) {
        addOneTimeTransaction(recurringTransaction);
        updateRecurringTransaction(recurringTransaction);
        updateUserBalance(recurringTransaction);
    }

    private void addOneTimeTransaction(RecurringTransaction recurringTransaction) {
        OneTimeTransaction oneTimeTransaction = OneTimeTransaction.builder()
                .user(recurringTransaction.getUser())
                .name(recurringTransaction.getName())
                .amount(recurringTransaction.getAmount())
                .currency(recurringTransaction.getCurrency())
                .income(recurringTransaction.getIncome())
                .date(LocalDate.now())
                .description(recurringTransaction.getDescription())
                .build();
        oneTimeTransactionRepository.save(oneTimeTransaction);
    }

    private void updateRecurringTransaction(RecurringTransaction recurringTransaction) {
//       TODO ?
//        Optional<?> interval = Optional.ofNullable(recurringTransaction.getInterval());
//        LocalDate newLastPaymentDate = recurringTransaction.getLastPaymentDate().plusDays(interval);
//        recurringTransaction.setLastPaymentDate(newLastPaymentDate);
//        recurringTransactionRepository.save(recurringTransaction);
    }

    private void updateUserBalance(RecurringTransaction recurringTransaction) {
        User user = recurringTransaction.getUser();
        BigDecimal currentBalance = new BigDecimal(userRepository.getUserBudget(user.getUserId()));
        BigDecimal transactionAmount = recurringTransaction.getIncome() ? recurringTransaction.getAmount() : recurringTransaction.getAmount().negate();

        String userCurrency = user.getCurrency().getIsoCode();
        String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();
        try {
            transactionAmount = currencyService.convertCurrency(transactionAmount, transactionCurrency, userCurrency);
        } catch (IOException e) {
            log.error("Currency conversion failed", e);
            transactionAmount = BigDecimal.ZERO;
        }

        BigDecimal newBalance = currentBalance.add(transactionAmount);
        user.setCurrentBudget(newBalance);
        userRepository.save(user);
    }


    public TransactionBudgetInfo getNextTransaction() {
//    TODO ?
        return null;
    }

    public BigDecimal getEstimatedBalanceAtTheEndOfTheMonth(int userId) {
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<TransactionBudgetInfo> transactions = getEstimatedBudgetInDateRange(userId, LocalDate.now(), endOfMonth);

        if (transactions.isEmpty()) {
            return new BigDecimal(userRepository.getUserBudget(userId));
        } else {
            return transactions.getLast().amount();
        }
    }

    public List<TransactionBudgetInfo> getEstimatedBudgetInDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        int currentBudget = userRepository.getUserBudget(userId);
        String userCurrency = userRepository.getUserCurrencyName(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency for user with ID %d not found", userId)));

        List<OneTimeTransaction> oneTimeTransactionsList = oneTimeTransactionRepository.getTransactionsInRange(userId, startDate, endDate);

        List<TransactionBudgetInfo> allTransactions = new ArrayList<>();

        for (OneTimeTransaction oneTimeTransaction : oneTimeTransactionsList) {
            String transactionCurrency = oneTimeTransaction.getCurrency().getIsoCode();
            BigDecimal transactionAmount;
            try {
                transactionAmount = currencyService.convertCurrency(oneTimeTransaction.getAmount(), transactionCurrency, userCurrency);

                allTransactions.add(new TransactionBudgetInfo(
                        oneTimeTransaction.getName(),
                        oneTimeTransaction.getDate(),
                        oneTimeTransaction.getIncome() ? transactionAmount : transactionAmount.negate()
                ));
            } catch (IOException e) {
                log.error("Currency conversion failed", e);
            }
        }

        if (endDate.isAfter(LocalDate.now())) {
            List<RecurringTransaction> rucurringTransactionsList = recurringTransactionRepository.getActiveTransactionsByUser(userId, startDate, endDate);

//            TODO: Add all recurring transactions to allTransactions using interval
//            TODO: Use Api to convert amounts in different currencies to specific one
//            for (RecurringTransaction recurringTransaction : rucurringTransactionsList) {
//                for () {
//                    allTransactions.add();
//                }
//            }
        }

        allTransactions.sort(Comparator.comparing(TransactionBudgetInfo::date));
        List<TransactionBudgetInfo> response = new ArrayList<>();

        BigDecimal budget = new BigDecimal(currentBudget);
        response.add(new TransactionBudgetInfo("", startDate, budget));

        for (TransactionBudgetInfo transaction : allTransactions) {
            budget.add(transaction.amount());
            response.add(new TransactionBudgetInfo(transaction.transactionName(), transaction.date(), budget));
        }

        return response;
    }
}
