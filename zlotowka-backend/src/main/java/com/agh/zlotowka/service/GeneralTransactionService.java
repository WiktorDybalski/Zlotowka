package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.PeriodEnum;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        updateRecurringTransaction(recurringTransaction);
        updateUserBalance(recurringTransaction);
        addOneTimeTransaction(recurringTransaction);
    }

    private void addOneTimeTransaction(RecurringTransaction recurringTransaction) {
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
    }

    private void updateRecurringTransaction(RecurringTransaction recurringTransaction) {
        PeriodEnum interval = recurringTransaction.getInterval();
        LocalDate newPaymentDate = interval.addToDate(recurringTransaction.getNextPaymentDate());

        if (newPaymentDate.isBefore(recurringTransaction.getFinalPaymentDate()))
            recurringTransaction.setNextPaymentDate(newPaymentDate);

        recurringTransactionRepository.save(recurringTransaction);
    }

    private void updateUserBalance(RecurringTransaction recurringTransaction) {
        User user = recurringTransaction.getUser();
        BigDecimal currentBalance = userRepository.getUserBudget(recurringTransaction.getUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with Id not found"));
        BigDecimal transactionAmount = recurringTransaction.getIsIncome() ? recurringTransaction.getAmount() : recurringTransaction.getAmount().negate();

        String userCurrency = user.getCurrency().getIsoCode();
        String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();
        try {
            transactionAmount = currencyService.convertCurrency(transactionAmount, transactionCurrency, userCurrency);

            BigDecimal newBalance = currentBalance.add(transactionAmount);
            user.setCurrentBudget(newBalance);
            userRepository.save(user);
        } catch (CurrencyConversionException e) {
            log.error("Currency conversion failed", e);
        } catch (Exception e) {
            log.error("Unexpected Exception in CurrencyService", e);
        }
    }

    public TransactionBudgetInfo getNextTransaction(int userId) {
        Optional<OneTimeTransaction> nextOneTimeTransaction = oneTimeTransactionRepository.getNextOneTimeTransactionByUser(userId, LocalDate.now());
        Optional<RecurringTransaction> nextRecurringTransaction = recurringTransactionRepository.getNextRecurringTransactionByUser(userId, LocalDate.now());

        TransactionBudgetInfo transaction1 = nextOneTimeTransaction.map(t ->
                new TransactionBudgetInfo(t.getName(), t.getDate(), t.getAmount(), t.getCurrency().getIsoCode())
        ).orElse(null);

        TransactionBudgetInfo transaction2 = nextRecurringTransaction.map(t ->
                new TransactionBudgetInfo(t.getName(), t.getNextPaymentDate(), t.getAmount(), t.getCurrency().getIsoCode())
        ).orElse(null);

        if (transaction1 == null) return transaction2;
        if (transaction2 == null) return transaction1;

        return transaction1.date().isBefore(transaction2.date()) ? transaction1 : transaction2;
    }

    public BigDecimal getEstimatedBalanceAtTheEndOfTheMonth(int userId) throws EntityNotFoundException {
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<TransactionBudgetInfo> transactions = getEstimatedBudgetInDateRange(userId, LocalDate.now(), endOfMonth);

        if (transactions.isEmpty()) {
            return userRepository.getUserBudget(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        } else {
            return transactions.getLast().amount();
        }
    }

    public List<TransactionBudgetInfo> getEstimatedBudgetInDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal budget = userRepository.getUserBudget(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));
        String userCurrency = userRepository.getUserCurrencyName(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency for user with ID %d not found", userId)));

        List<TransactionBudgetInfo> allTransactions = new ArrayList<>();

        transformOneTimeTransactions(userId, startDate, endDate, userCurrency, allTransactions);
        if (endDate.isAfter(LocalDate.now())) {
            recurringTransactionsIntoOneTime(userId, startDate, endDate, userCurrency, allTransactions);
        }

        allTransactions.sort(Comparator.comparing(TransactionBudgetInfo::date));
        List<TransactionBudgetInfo> transactionBudgetInfoList = new ArrayList<>();

        transactionBudgetInfoList.add(new TransactionBudgetInfo("", startDate, budget, userCurrency));

        for (TransactionBudgetInfo transaction : allTransactions) {
            BigDecimal updateBudget = budget.add(transaction.amount());
            transactionBudgetInfoList.add(new TransactionBudgetInfo(transaction.transactionName(), transaction.date(), updateBudget, userCurrency));
        }
        return transactionBudgetInfoList;
    }

    private void recurringTransactionsIntoOneTime(int userId, LocalDate startDate, LocalDate endDate, String userCurrency, List<TransactionBudgetInfo> allTransactions) {
        List<RecurringTransaction> rucurringTransactionsList = recurringTransactionRepository.getActiveTransactionsByUser(userId, startDate, endDate);

        for (RecurringTransaction recurringTransaction : rucurringTransactionsList) {

            PeriodEnum period = recurringTransaction.getInterval();
            LocalDate nextPaymentDate = period.addToDate(recurringTransaction.getNextPaymentDate());
            String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();
            BigDecimal transactionAmount;

            while (nextPaymentDate.isBefore(endDate)) {
                try {
                    transactionAmount = currencyService.convertCurrency(recurringTransaction.getAmount(), transactionCurrency, userCurrency);

                    allTransactions.add(new TransactionBudgetInfo(
                            recurringTransaction.getName(),
                            nextPaymentDate,
                            recurringTransaction.getIsIncome() ? transactionAmount : transactionAmount.negate(),
                            userCurrency
                    ));
                } catch (CurrencyConversionException e) {
                    log.error("Currency conversion failed", e);
                } catch (Exception e) {
                    log.error("Unexpected error from CurrencyService", e);
                }

                nextPaymentDate = period.addToDate(nextPaymentDate);
            }
        }
    }

    private void transformOneTimeTransactions(int userId, LocalDate startDate, LocalDate endDate, String userCurrency, List<TransactionBudgetInfo> allTransactions) {
        List<OneTimeTransaction> oneTimeTransactionsList = oneTimeTransactionRepository.getTransactionsInRange(userId, startDate, endDate);

        for (OneTimeTransaction oneTimeTransaction : oneTimeTransactionsList) {
            String transactionCurrency = oneTimeTransaction.getCurrency().getIsoCode();
            BigDecimal transactionAmount;
            try {
                transactionAmount = currencyService.convertCurrency(oneTimeTransaction.getAmount(), transactionCurrency, userCurrency);

                allTransactions.add(new TransactionBudgetInfo(
                        oneTimeTransaction.getName(),
                        oneTimeTransaction.getDate(),
                        oneTimeTransaction.getIsIncome() ? transactionAmount : transactionAmount.negate(),
                        userCurrency
                ));
            } catch (CurrencyConversionException e) {
                log.error("Currency conversion failed", e);
            } catch (Exception e) {
                log.error("Unexpected error from CurrencyService", e);
            }
        }
    }
}
