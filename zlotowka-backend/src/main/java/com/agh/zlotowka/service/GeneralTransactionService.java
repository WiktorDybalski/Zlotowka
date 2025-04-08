package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RevenuesAndExpensesResponse;
import com.agh.zlotowka.dto.TransactionBudgetInfo;
import com.agh.zlotowka.dto.UserDataInDateRangeRequest;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.PeriodEnum;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
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
//@EnableScheduling
@RequiredArgsConstructor
public class GeneralTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final UserService userService;

    // TODO Add oneTimeTransaction in scheduled task
//    @Scheduled(cron = "0 0 19 * * ?") // Everyday at 19:00
    public void addRecurringTransactions() {
        log.info("Adding transactions using sheduled task...");
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findDueRecurringTransactions();
        List<OneTimeTransaction> oneTimeTransactionsToAdd = oneTimeTransactionRepository.findTransactionsToday();
        addOneTimeTransactionToUserBudget(oneTimeTransactionsToAdd);
        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            updateDataWithNewTransaction(recurringTransaction);
        }
    }

    private void addOneTimeTransactionToUserBudget(List<OneTimeTransaction> oneTimeTransactionsToAdd) {
        log.info("Adding oneTimeTransactions to user budget...");
        for (OneTimeTransaction transaction : oneTimeTransactionsToAdd) {
            userService.addTransactionAmountToBudget(transaction.getTransactionId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
    }

    @Transactional
    public void updateDataWithNewTransaction(RecurringTransaction recurringTransaction) {
        log.info("Updating data with new transaction from recurring...");
        updateRecurringTransaction(recurringTransaction);
        updateUserBalance(recurringTransaction);
        addOneTimeTransactionFromRecurring(recurringTransaction);
    }

    private void addOneTimeTransactionFromRecurring(RecurringTransaction recurringTransaction) {
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
    }

    private void updateRecurringTransaction(RecurringTransaction recurringTransaction) {
        log.info("Updating recurring transaction...");
        PeriodEnum interval = recurringTransaction.getInterval();
        LocalDate newPaymentDate = interval.addToDate(recurringTransaction.getNextPaymentDate(), recurringTransaction.getFirstPaymentDate());

        if (newPaymentDate.isBefore(recurringTransaction.getFinalPaymentDate()))
            recurringTransaction.setNextPaymentDate(newPaymentDate);

        recurringTransactionRepository.save(recurringTransaction);
    }

    private void updateUserBalance(RecurringTransaction recurringTransaction) {
//        User user = recurringTransaction.getUser();
//        BigDecimal currentBalance = userRepository.getUserBudget(recurringTransaction.getUser().getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User with Id not found"));
//        BigDecimal transactionAmount = recurringTransaction.getIsIncome() ? recurringTransaction.getAmount() : recurringTransaction.getAmount().negate();
//
//        String userCurrency = user.getCurrency().getIsoCode();
//        String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();
//        try {
//            transactionAmount = currencyService.convertCurrency(transactionAmount, transactionCurrency, userCurrency);
//
//            BigDecimal newBalance = currentBalance.add(transactionAmount);
//            user.setCurrentBudget(newBalance);
//            userRepository.save(user);
//        } catch (CurrencyConversionException e) {
//            log.error("Currency conversion failed", e);
//        } catch (Exception e) {
//            log.error("Unexpected Exception in CurrencyService", e);
//        }
        userService.addTransactionAmountToBudget(
                recurringTransaction.getCurrency().getCurrencyId(),
                recurringTransaction.getAmount(),
                recurringTransaction.getIsIncome(),
                recurringTransaction.getUser());
    }

    public TransactionBudgetInfo getNextTransaction(Integer userId, Boolean isIncome) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        if (isIncome == null) {
            throw new IllegalArgumentException("isIncome cannot be null");
        }

        Optional<OneTimeTransaction> nextOneTimeTransaction;
        Optional<RecurringTransaction> nextRecurringTransaction;
        if (isIncome) {
            nextOneTimeTransaction = oneTimeTransactionRepository.getNextIncomeOneTimeTransactionByUser(userId, LocalDate.now());
            nextRecurringTransaction = recurringTransactionRepository.getNextIncomeRecurringTransactionByUser(userId, LocalDate.now());
        } else {
            nextOneTimeTransaction = oneTimeTransactionRepository.getNextExpenseOneTimeTransactionByUser(userId, LocalDate.now());
            nextRecurringTransaction = recurringTransactionRepository.getNextExpenseRecurringTransactionByUser(userId, LocalDate.now());
        }

        TransactionBudgetInfo transaction1 = nextOneTimeTransaction.map(t ->
                new TransactionBudgetInfo(t.getName(), t.getDate(), t.getAmount(), t.getIsIncome(), t.getCurrency().getIsoCode())
        ).orElse(null);

        TransactionBudgetInfo transaction2 = nextRecurringTransaction.map(t ->
                new TransactionBudgetInfo(t.getName(), t.getNextPaymentDate(), t.getAmount(), t.getIsIncome(), t.getCurrency().getIsoCode())
        ).orElse(null);

        if (transaction1 == null && transaction2 == null) {
            return new TransactionBudgetInfo("No transaction", LocalDate.now(), BigDecimal.ZERO, false, "PLN");
        }

        if (transaction1 == null) return transaction2;
        if (transaction2 == null) return transaction1;

        return transaction1.date().isBefore(transaction2.date()) ? transaction1 : transaction2;
    }

    public BigDecimal getEstimatedBalanceAtTheEndOfTheMonth(int userId) throws EntityNotFoundException {
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<TransactionBudgetInfo> transactions = getEstimatedBudgetInDateRange(new UserDataInDateRangeRequest(userId, LocalDate.now(), endOfMonth));

        if (transactions.isEmpty()) {
            return userRepository.getUserBudget(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        } else {
            return transactions.getLast().amount();
        }
    }

    public List<TransactionBudgetInfo> getEstimatedBudgetInDateRange(UserDataInDateRangeRequest request) {
        BigDecimal budget = userRepository.getUserBudget(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", request.userId())));
        String userCurrency = userRepository.getUserCurrencyName(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency for user with ID %d not found", request.userId())));

        List<TransactionBudgetInfo> allTransactions = new ArrayList<>();

        transformOneTimeTransactions(request.userId(), request.startDate(), request.endDate(), userCurrency, allTransactions);
        if (request.endDate().isAfter(LocalDate.now())) {
            recurringTransactionsIntoOneTime(request.userId(), request.startDate(), request.endDate(), userCurrency, allTransactions);
        }

        allTransactions.sort(Comparator.comparing(TransactionBudgetInfo::date));
        List<TransactionBudgetInfo> transactionBudgetInfoList = new ArrayList<>();
        BigDecimal updatedBudget = budget;

        for (TransactionBudgetInfo transaction : allTransactions) {
            updatedBudget = updatedBudget.add(transaction.amount());
            transactionBudgetInfoList.add(new TransactionBudgetInfo(transaction.transactionName(), transaction.date(), updatedBudget, transaction.isIncome(), userCurrency));
        }
        return transactionBudgetInfoList;
    }

    private void recurringTransactionsIntoOneTime(int userId, LocalDate startDate, LocalDate endDate, String userCurrency, List<TransactionBudgetInfo> allTransactions) {
        List<RecurringTransaction> rucurringTransactionsList = recurringTransactionRepository.getActiveTransactionsByUser(userId, startDate, endDate);

        for (RecurringTransaction recurringTransaction : rucurringTransactionsList) {

            PeriodEnum period = recurringTransaction.getInterval();
            LocalDate nextPaymentDate = period.addToDate(recurringTransaction.getNextPaymentDate(), recurringTransaction.getFirstPaymentDate());
            String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();
            BigDecimal transactionAmount;

            while (nextPaymentDate.isBefore(endDate) && nextPaymentDate.isBefore(recurringTransaction.getFinalPaymentDate())) {
                try {
                    transactionAmount = currencyService.convertCurrency(recurringTransaction.getAmount(), transactionCurrency, userCurrency);

                    allTransactions.add(new TransactionBudgetInfo(
                            recurringTransaction.getName(),
                            nextPaymentDate,
                            recurringTransaction.getIsIncome() ? transactionAmount : transactionAmount.negate(),
                            recurringTransaction.getIsIncome(),
                            userCurrency
                    ));
                } catch (CurrencyConversionException e) {
                    log.error("Currency conversion failed", e);
                } catch (Exception e) {
                    log.error("Unexpected error from CurrencyService", e);
                }

                nextPaymentDate = period.addToDate(nextPaymentDate, recurringTransaction.getFirstPaymentDate());
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
                        oneTimeTransaction.getIsIncome(),
                        userCurrency
                ));
            } catch (CurrencyConversionException e) {
                log.error("Currency conversion failed", e);
            } catch (Exception e) {
                log.error("Unexpected error from CurrencyService", e);
            }
        }
    }

    public RevenuesAndExpensesResponse getRevenuesAndExpensesInRange(UserDataInDateRangeRequest request) {
        String userCurrency = userRepository.getUserCurrencyName(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency for user with ID %d not found", request.userId())));

        List<TransactionBudgetInfo> allTransactions = new ArrayList<>();

        transformOneTimeTransactions(request.userId(), request.startDate(), request.endDate(), userCurrency, allTransactions);
        if (request.endDate().isAfter(LocalDate.now())) {
            recurringTransactionsIntoOneTime(request.userId(), request.startDate(), request.endDate(), userCurrency, allTransactions);
        }

        BigDecimal revenue = allTransactions.stream()
                .filter(TransactionBudgetInfo::isIncome)
                .map(TransactionBudgetInfo::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = allTransactions.stream()
                .filter(t -> !t.isIncome())
                .map(TransactionBudgetInfo::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenuesAndExpensesResponse(revenue, expenses);
    }
}
