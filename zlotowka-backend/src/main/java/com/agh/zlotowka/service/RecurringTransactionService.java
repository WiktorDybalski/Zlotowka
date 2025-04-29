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

    @Transactional
    public RecurringTransactionDTO createTransaction(RecurringTransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", request.userId())));
        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

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
        log.info("Updating recurring transaction with transactionId {}", transactionId);

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
        log.info("Deleting transaction with transactionId {}", id);
        RecurringTransaction transaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", id)));

        recurringTransactionRepository.delete(transaction);
    }

    private RecurringTransaction findTransactionById(int transactionId) {
        return recurringTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Transaction with Id %d not found", transactionId)));
    }
    public void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }

    public void validateOwnershipById(int transactionId, CustomUserDetails userDetails) {
        RecurringTransaction tx = recurringTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Transaction with Id %d not found", transactionId)));
        if (!tx.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
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
        userService.addTransactionAmountToBudget(currency.getCurrencyId(),
                request.amount().multiply(new BigDecimal(transactionsList.size())), request.isIncome(), user);
        oneTimeTransactionRepository.saveAll(transactionsList);
    }

    private void validateFirstAndFinalDates(LocalDate firstPaymentDate, LocalDate lastPaymentDate) {
        if (firstPaymentDate.isAfter(lastPaymentDate)) {
            throw new IllegalArgumentException("First payment date must be before the last payment date");
        }
    }

    private void validateTransactionType(RecurringTransactionRequest request, RecurringTransaction transaction) {
        if (request.isIncome() != transaction.getIsIncome()) {
            throw new IllegalArgumentException("Cannot change transaction type");
        }
    }

    private void validateTransactionState(RecurringTransactionRequest request, RecurringTransaction transaction, PeriodEnum newInterval) {
        if (!transaction.getFirstPaymentDate().isAfter(LocalDate.now())) {
            if (!request.firstPaymentDate().equals(transaction.getFirstPaymentDate())) {
                throw new IllegalArgumentException("Cannot change first payment date of transaction that has already started");
            }

            if (!newInterval.equals(transaction.getInterval())) {
                throw new IllegalArgumentException("Cannot change interval of transaction that has already started");
            }

            if (!transaction.getFinalPaymentDate().isAfter(LocalDate.now())
                    && !transaction.getFinalPaymentDate().equals(request.firstPaymentDate())) {
                throw new IllegalArgumentException("Cannot change final payment date of transaction that has already finished");
            }
        }
    }

    private void updateCurrencyIfNeeded(RecurringTransactionRequest request, RecurringTransaction transaction) {
        if (!request.currencyId().equals(transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));
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
            throw new IllegalArgumentException(String.format("User Id %d does not match the transaction owner", requestSenderId));
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
