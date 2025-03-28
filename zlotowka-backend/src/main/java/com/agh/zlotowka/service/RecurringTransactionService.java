package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    public RecurringTransaction createTransaction(RecurringTransactionRequest request) {
        log.info("Creating transaction with request: {}", request);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", request.userId())));
        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

        RecurringTransaction transaction =  RecurringTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .isIncome(request.isIncome())
                .interval(PeriodEnum.fromPeriod(request.interval()))
                .finalPaymentDate(request.firstPaymentDate())
                .nextPaymentDate(PeriodEnum.fromPeriod(request.interval()).addToDate(request.firstPaymentDate()))
                .finalPaymentDate(request.firstPaymentDate())
                .description(request.description())
                .build();

        recurringTransactionRepository.save(transaction);
        log.info("New transaction with Id {} has been created", transaction.getTransactionId());
        return transaction;
    }

    public RecurringTransaction getTransaction(Integer id) {
        return recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", id)));
    }

    public RecurringTransaction updateTransaction(RecurringTransactionRequest request) {
        log.info("Updating transaction with transactionId {}", request.transactionId());

        RecurringTransaction transaction = recurringTransactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", request.transactionId())));

        if (!Objects.equals(request.currencyId(), transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));

            transaction.setCurrency(currency);
        }

        transaction.setName(request.name());
        transaction.setAmount(request.amount());

        recurringTransactionRepository.save(transaction);
        return transaction;
    }

    public void deleteTransaction(Integer id) {
        log.info("Deleting transaction with transactionId {}", id);
        RecurringTransaction transaction = getTransaction(id);
        recurringTransactionRepository.delete(transaction);
    }
}
