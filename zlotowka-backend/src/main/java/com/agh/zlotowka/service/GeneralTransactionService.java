package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.*;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.PeriodEnum;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class GeneralTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final ScheduledTransactionService scheduledTransactionService;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final PeriodService periodService;

    @Scheduled(cron = "00 01 00 * * ?")
    public void addRecurringTransactions() {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findDueRecurringTransactions();
        List<OneTimeTransaction> oneTimeTransactionsToAdd = oneTimeTransactionRepository.findTransactionsToday();
        scheduledTransactionService.addOneTimeTransactionToUserBudget(oneTimeTransactionsToAdd);
        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            scheduledTransactionService.updateDataWithNewTransaction(recurringTransaction);
        }
    }

    public TransactionBudgetInfo getNextTransaction(Integer userId, Boolean isIncome) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        if (isIncome == null) {
            throw new IllegalArgumentException("Parametr isIncome nie może być pusty");
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
            return new TransactionBudgetInfo("Brak transakcji", LocalDate.now(), BigDecimal.ZERO, false, "PLN");
        }

        if (transaction1 == null) return transaction2;
        if (transaction2 == null) return transaction1;

        return transaction1.date().isBefore(transaction2.date()) ? transaction1 : transaction2;
    }

    public BigDecimal getEstimatedBalanceAtTheEndOfTheMonth(int userId) throws EntityNotFoundException {
        LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<SinglePlotData> transactions = getEstimatedBudgetInDateRange(new UserDataInDateRangeRequest(userId, LocalDate.now(), endOfMonth));

        if (transactions.isEmpty()) {
            return userRepository.getUserBudget(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", userId)));

        } else {
            return transactions.getLast().amount();
        }
    }

    public List<SinglePlotData> getEstimatedBudgetInDateRange(UserDataInDateRangeRequest request) {
        BigDecimal budget = userRepository.getUserBudget(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));
        String userCurrency = userRepository.getUserCurrencyName(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono waluty dla użytkownika o ID %d", request.userId())));

        List<TransactionBudgetInfo> futureTransactions = new ArrayList<>();
        List<TransactionBudgetInfo> pastTransactions = new ArrayList<>();

        transformOneTimeTransactions(request.userId(), request.startDate(), request.endDate(), userCurrency, futureTransactions, pastTransactions);

        if (request.endDate().isAfter(LocalDate.now())) {
            recurringTransactionsIntoOneTime(request.userId(), request.startDate(), request.endDate(), userCurrency, futureTransactions);
        }

        futureTransactions.sort(Comparator.comparing(TransactionBudgetInfo::date));
        pastTransactions.sort(Comparator.comparing(TransactionBudgetInfo::date).reversed());

        List<SinglePlotData> transactionBudgetInfoList = new ArrayList<>();
        BigDecimal updatedBudget = budget;

        for (TransactionBudgetInfo transaction : pastTransactions) {
            transactionBudgetInfoList.add(new SinglePlotData(transaction.date(), updatedBudget, userCurrency));
            updatedBudget = updatedBudget.subtract(transaction.amount());
        }

        transactionBudgetInfoList.add(new SinglePlotData(request.startDate(), updatedBudget, userCurrency));

        updatedBudget = budget;
        transactionBudgetInfoList.add(new SinglePlotData(LocalDate.now(), updatedBudget, userCurrency));

        for (TransactionBudgetInfo transaction : futureTransactions) {
            updatedBudget = updatedBudget.add(transaction.amount());
            transactionBudgetInfoList.add(new SinglePlotData(transaction.date(), updatedBudget, userCurrency));
        }

        transactionBudgetInfoList.add(new SinglePlotData(request.endDate(), updatedBudget, userCurrency));

        Map<LocalDate, SinglePlotData> uniqueByDate = new TreeMap<>();
        for (SinglePlotData data : transactionBudgetInfoList) {
            uniqueByDate.put(data.date(), data);
        }

        return new ArrayList<>(uniqueByDate.values());
    }

    public void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Dostęp zabroniony");
        }
    }

    private void recurringTransactionsIntoOneTime(int userId, LocalDate startDate, LocalDate endDate, String userCurrency, List<TransactionBudgetInfo> allTransactions) {
        List<RecurringTransaction> rucurringTransactionsList = recurringTransactionRepository.getActiveTransactionsByUser(userId, startDate, endDate);

        for (RecurringTransaction recurringTransaction : rucurringTransactionsList) {

            PeriodEnum period = recurringTransaction.getInterval();
            LocalDate nextPaymentDate = recurringTransaction.getNextPaymentDate();
            String transactionCurrency = recurringTransaction.getCurrency().getIsoCode();

            while (!nextPaymentDate.isAfter(endDate) && !nextPaymentDate.isAfter(recurringTransaction.getFinalPaymentDate())) {
                addToAllTransactions(userCurrency, allTransactions, recurringTransaction, transactionCurrency, nextPaymentDate);

                nextPaymentDate = period.addToDate(nextPaymentDate, recurringTransaction.getFirstPaymentDate());
            }
        }
    }

    private void addToAllTransactions(String userCurrency, List<TransactionBudgetInfo> allTransactions, RecurringTransaction recurringTransaction, String transactionCurrency, LocalDate nextPaymentDate) {
        try {
            BigDecimal transactionAmount = currencyService.convertCurrency(recurringTransaction.getAmount(), transactionCurrency, userCurrency);

            allTransactions.add(new TransactionBudgetInfo(
                    recurringTransaction.getName(),
                    nextPaymentDate,
                    recurringTransaction.getIsIncome() ? transactionAmount : transactionAmount.negate(),
                    recurringTransaction.getIsIncome(),
                    userCurrency
            ));
        } catch (CurrencyConversionException e) {
            log.error("Currency conversion failed", e);
        }
    }

    private void transformOneTimeTransactions(int userId, LocalDate startDate, LocalDate endDate, String userCurrency, List<TransactionBudgetInfo> futureTransaction, List<TransactionBudgetInfo> pastTransactions) {
        List<OneTimeTransaction> oneTimeTransactionsList = oneTimeTransactionRepository.getTransactionsInRange(userId, startDate, endDate);

        for (OneTimeTransaction oneTimeTransaction : oneTimeTransactionsList) {
            String transactionCurrency = oneTimeTransaction.getCurrency().getIsoCode();
            BigDecimal transactionAmount;
            try {
                transactionAmount = currencyService.convertCurrency(oneTimeTransaction.getAmount(), transactionCurrency, userCurrency);

                TransactionBudgetInfo transactionBudgetInfo = new TransactionBudgetInfo(
                        oneTimeTransaction.getName(),
                        oneTimeTransaction.getDate(),
                        oneTimeTransaction.getIsIncome() ? transactionAmount : transactionAmount.negate(),
                        oneTimeTransaction.getIsIncome(),
                        userCurrency
                );

                if (oneTimeTransaction.getDate().isAfter(LocalDate.now())) {
                    futureTransaction.add(transactionBudgetInfo);
                } else {
                    pastTransactions.add(transactionBudgetInfo);
                }

            } catch (CurrencyConversionException e) {
                log.error("Currency conversion failed", e);
            }
        }
    }

    public RevenuesAndExpensesResponse getRevenuesAndExpensesInRange(UserDataInDateRangeRequest request) {
        String userCurrency = userRepository.getUserCurrencyName(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));

        List<TransactionBudgetInfo> futureTransactions = new ArrayList<>();
        List<TransactionBudgetInfo> pastTransactions = new ArrayList<>();

        transformOneTimeTransactions(request.userId(), request.startDate(), request.endDate(), userCurrency, futureTransactions, pastTransactions);
        if (request.endDate().isAfter(LocalDate.now())) {
            recurringTransactionsIntoOneTime(request.userId(), request.startDate(), request.endDate(), userCurrency, futureTransactions);
        }

        List<TransactionBudgetInfo> allTransactions = new ArrayList<>();
        allTransactions.addAll(futureTransactions);
        allTransactions.addAll(pastTransactions);

        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (TransactionBudgetInfo tx : allTransactions) {
            if (tx.isIncome()) {
                revenue = revenue.add(tx.amount());
            } else {
                expenses = expenses.add(tx.amount());
            }
        }

        return new RevenuesAndExpensesResponse(revenue, expenses, userCurrency);
    }

    public MonthlySummaryDTO getMonthlySummary(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        BigDecimal monthlyIncome = oneTimeTransactionRepository.getMonthlyIncomeByUser(userId, startDate);
        BigDecimal monthlyExpenses = oneTimeTransactionRepository.getMonthlyExpensesByUser(userId, startDate);
        return new MonthlySummaryDTO(monthlyIncome, monthlyExpenses, monthlyIncome.subtract(monthlyExpenses), user.getCurrency().getIsoCode());
    }

    public BigDecimal getCurrentBalance(Integer userId) {
        return userRepository.getUserBudget(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", userId)));
    }

    public PaginatedTransactionsDTO getPageTransactionsByUserId(Integer userId, Integer page, Integer limit, LocalDate startDate, LocalDate endDate) {
        if (page < 1 || limit <= 0) {
            throw new IllegalArgumentException("Strona musi być >= 1, a limit musi być > 0");
        }

        validateFirstAndFinalDates(startDate, endDate);

        List<TransactionDTO> allTransactions = getAllTransactionsByUserId(userId, startDate, endDate);
        int total = allTransactions.size();
        int totalPages = (int) Math.ceil((double) total / limit);

        if (page > totalPages) {
            return new PaginatedTransactionsDTO(Collections.emptyList(), total, page, totalPages);
        }

        int fromIndex = Math.max(0, (page - 1) * limit);
        int toIndex = Math.min(fromIndex + limit, total);

        List<TransactionDTO> paginatedTransactions = allTransactions.subList(fromIndex, toIndex);

        return new PaginatedTransactionsDTO(paginatedTransactions, total, page, totalPages);
    }

    private List<TransactionDTO> getAllTransactionsByUserId(Integer userId, LocalDate startDate, LocalDate endDate) {
        List<OneTimeTransaction> oneTimeTransactions = oneTimeTransactionRepository.getTransactionsInRange(userId, startDate, endDate);
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.getActiveTransactionsByUser(userId, startDate, endDate);

        return Stream.concat(
                        oneTimeTransactions.stream().map(this::mapOneTimeTransaction),
                        recurringTransactions.stream().flatMap(r -> generateRecurringTransactionInstances(r, startDate, endDate).stream())
                ).sorted(Comparator.comparing(TransactionDTO::getDate))
                .toList();
    }

    private TransactionDTO mapOneTimeTransaction(OneTimeTransaction t) {
        return new TransactionDTO(
                t.getTransactionId(),
                t.getUser().getUserId(),
                t.getName(),
                t.getAmount(),
                t.getCurrency(),
                t.getIsIncome(),
                t.getDate(),
                t.getDescription(),
                periodService.getPeriod(PeriodEnum.ONCE)
        );
    }

    private List<TransactionDTO> generateRecurringTransactionInstances(RecurringTransaction recurringTransaction, LocalDate startDate, LocalDate endDate) {
        List<TransactionDTO> list = new ArrayList<>();
        PeriodEnum period = recurringTransaction.getInterval();
        LocalDate nextDate = recurringTransaction.getNextPaymentDate();

        if (!nextDate.isBefore(startDate) && !nextDate.isAfter(endDate) && !nextDate.isAfter(recurringTransaction.getFinalPaymentDate())) {
            list.add(
                    PeriodTransactionDTO.builder()
                            .transactionId(recurringTransaction.getTransactionId())
                            .userId(recurringTransaction.getUser().getUserId())
                            .name(recurringTransaction.getName())
                            .amount(recurringTransaction.getAmount())
                            .currency(recurringTransaction.getCurrency())
                            .isIncome(recurringTransaction.getIsIncome())
                            .date(nextDate)
                            .description(recurringTransaction.getDescription())
                            .period(periodService.getPeriod(period))
                            .firstPaymentDate(recurringTransaction.getFirstPaymentDate())
                            .nextPaymentDate(nextDate)
                            .finalPaymentDate(recurringTransaction.getFinalPaymentDate())
                            .build()
            );
        }
        return list;
    }


    private void validateFirstAndFinalDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data początkowa nie może być późniejsza niż data końcowa");
        }
    }
}
