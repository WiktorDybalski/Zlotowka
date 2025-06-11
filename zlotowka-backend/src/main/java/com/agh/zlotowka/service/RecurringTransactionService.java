package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserService userService;
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final SystemNotificationService systemNotificationService;

    @Transactional
    public RecurringTransactionDTO createTransaction(RecurringTransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));
        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono waluty o ID %d", request.currencyId())));

        validateFirstAndFinalDates(request.firstPaymentDate(), request.lastPaymentDate());
        PeriodEnum interval = PeriodEnum.fromPeriod(Period.parse(request.interval()));

        RecurringTransaction transaction =  RecurringTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .isIncome(request.isIncome())
                .interval(interval)
                .firstPaymentDate(request.firstPaymentDate())
                .nextPaymentDate(calculateNextPaymentDateAndAddOverDueTransaction(request, user, currency, interval))
                .finalPaymentDate(request.lastPaymentDate())
                .description(request.description())
                .build();

        recurringTransactionRepository.save(transaction);
        log.info("New transaction with Id {} has been created", transaction.getTransactionId());
        return getRecurringTransactionDTO(transaction);
    }

    public RecurringTransactionDTO getTransaction(Integer id) {
        return getRecurringTransactionDTO(findTransactionById(id));
    }

    @Transactional
    public RecurringTransactionDTO updateTransaction(RecurringTransactionRequest request, int transactionId) {
        RecurringTransaction transaction = findTransactionById(transactionId);
        validateTransactionOwnership(request.userId(), transaction.getUser().getUserId());
        validateTransactionType(request, transaction);

        PeriodEnum newInterval = PeriodEnum.fromPeriod(Period.parse(request.interval()));
        validateFirstAndFinalDates(request.firstPaymentDate(), request.lastPaymentDate());
        validateTransactionState(request, transaction, newInterval);

        updateCurrencyIfNeeded(request, transaction);
        updatePaymentDatesIfNeeded(request, transaction, newInterval);

        transaction.setName(request.name());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setInterval(newInterval);
        transaction.setFinalPaymentDate(request.lastPaymentDate());

        recurringTransactionRepository.save(transaction);
        return getRecurringTransactionDTO(transaction);
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        RecurringTransaction transaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono transakcji o ID %d", id)));

        recurringTransactionRepository.delete(transaction);
    }

    private RecurringTransaction findTransactionById(int transactionId) {
        return recurringTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Nie znaleziono transakcji o ID %d", transactionId)));
    }
    public void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Dostęp zabroniony");
        }
    }

    public void validateOwnershipById(int transactionId, CustomUserDetails userDetails) {
        RecurringTransaction tx = recurringTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Nie znaleziono transakcji o ID %d", transactionId)));
        if (!tx.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Dostęp zabroniony");
        }
    }

    private LocalDate calculateNextPaymentDateAndAddOverDueTransaction(RecurringTransactionRequest request, User user, Currency currency, PeriodEnum interval) {
        if (request.firstPaymentDate().isAfter(LocalDate.now()))
            return request.firstPaymentDate();

        LocalDate nextPaymentDate;
        LocalDate date = request.firstPaymentDate();
        List<OneTimeTransaction> transactionsList = new ArrayList<>();

        while (!date.isAfter(LocalDate.now())) {
            OneTimeTransaction transaction = OneTimeTransaction.builder()
                    .user(user)
                    .name(request.name())
                    .amount(request.amount())
                    .currency(currency)
                    .isIncome(request.isIncome())
                    .date(date)
                    .description(request.description())
                    .build();

            transactionsList.add(transaction);
            nextPaymentDate  = interval.addToDate(date, request.firstPaymentDate());

            if (!nextPaymentDate.isAfter(request.lastPaymentDate()))
                date = nextPaymentDate;
            else
                break;
        }
        addOverdueTransactionsToBudget(currency, request, transactionsList, user);
        return date;
    }

    private void addOverdueTransactionsToBudget(Currency currency, RecurringTransactionRequest request, List<OneTimeTransaction> transactionsList, User user) {
        BigDecimal oldBudget = user.getCurrentBudget();
        userService.addTransactionAmountToBudget(currency.getCurrencyId(),
                request.amount().multiply(new BigDecimal(transactionsList.size())), request.isIncome(), user);
        BigDecimal newBudget = user.getCurrentBudget();
        if (oldBudget.compareTo(BigDecimal.ZERO) >= 0 && newBudget.compareTo(BigDecimal.ZERO) < 0) {
            systemNotificationService.checkUserBalanceAndSendWarning(user);
        }
        oneTimeTransactionRepository.saveAll(transactionsList);
    }

    private void validateFirstAndFinalDates(LocalDate firstPaymentDate, LocalDate lastPaymentDate) {
        if (firstPaymentDate.isAfter(lastPaymentDate)) {
            throw new IllegalArgumentException("Data pierwszej płatności musi być przed datą ostatniej płatności");
        }
    }

    private void validateTransactionType(RecurringTransactionRequest request, RecurringTransaction transaction) {
        if (request.isIncome() != transaction.getIsIncome()) {
            throw new IllegalArgumentException("Nie można zmienić typu transakcji");
        }
    }

    private void validateTransactionState(RecurringTransactionRequest request, RecurringTransaction transaction, PeriodEnum newInterval) {
        if (!transaction.getFirstPaymentDate().isAfter(LocalDate.now())) {
            if (!request.firstPaymentDate().equals(transaction.getFirstPaymentDate())) {
                throw new IllegalArgumentException("Nie można zmienić daty pierwszej płatności transakcji, która już się rozpoczęła");
            }

            if (!newInterval.equals(transaction.getInterval())) {
                throw new IllegalArgumentException("Nie można zmienić interwału transakcji, która już się rozpoczęła");
            }

            if (!transaction.getFinalPaymentDate().isAfter(LocalDate.now())
                    && !transaction.getFinalPaymentDate().equals(request.firstPaymentDate())) {
                throw new IllegalArgumentException("Nie można zmienić daty końcowej płatności transakcji, która już się zakończyła");
            }
        }
    }

    private void updateCurrencyIfNeeded(RecurringTransactionRequest request, RecurringTransaction transaction) {
        if (!request.currencyId().equals(transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono waluty o ID: " + request.currencyId()));
            transaction.setCurrency(currency);
        }
    }

    private void updatePaymentDatesIfNeeded(RecurringTransactionRequest request, RecurringTransaction transaction, PeriodEnum newInterval) {
        if (!request.firstPaymentDate().equals(transaction.getFirstPaymentDate())) {
            transaction.setFirstPaymentDate(request.firstPaymentDate());
            transaction.setNextPaymentDate(
                    calculateNextPaymentDateAndAddOverDueTransaction(request, transaction.getUser(), transaction.getCurrency(), newInterval)
            );
        }
    }

    private void validateTransactionOwnership(Integer requestSenderId, Integer transactionOwner) {
        if (!requestSenderId.equals(transactionOwner))
            throw new IllegalArgumentException(String.format("ID użytkownika %d nie odpowiada właścicielowi transakcji", requestSenderId));
    }

    private RecurringTransactionDTO getRecurringTransactionDTO(RecurringTransaction transaction) {
        return RecurringTransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .name(transaction.getName())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .isIncome(transaction.getIsIncome())
                .firstPaymentDate(transaction.getFirstPaymentDate())
                .nextPaymentDate(transaction.getNextPaymentDate())
                .finalPaymentDate(transaction.getFinalPaymentDate())
                .interval(transaction.getInterval())
                .description(transaction.getDescription())
                .build();
    }
}
