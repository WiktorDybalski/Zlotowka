package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Transactional
    public RecurringTransactionDTO createTransaction(RecurringTransactionRequest request) {
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
                .interval(PeriodEnum.fromPeriod(Period.parse(request.interval())))
                .firstPaymentDate(request.firstPaymentDate())
                .nextPaymentDate(request.firstPaymentDate())
                .finalPaymentDate(request.lastPaymentDate())
                .description(request.description())
                .build();

        recurringTransactionRepository.save(transaction);
        log.info("New transaction with Id {} has been created", transaction.getTransactionId());
        return getRecurringTransactionDTO(transaction);
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

    public RecurringTransactionDTO getTransaction(Integer id) {
        return getRecurringTransactionDTO(recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", id))));
    }

    @Transactional
    public RecurringTransactionDTO updateTransaction(RecurringTransactionRequest request, int transactionId) {
        log.info("Updating recurring transaction with transactionId {}", transactionId);

        RecurringTransaction transaction = recurringTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", transactionId)));

        if (request.isIncome() != transaction.getIsIncome()) {
            throw new IllegalArgumentException("Cannot change transaction type");
        }

        if (transaction.getFirstPaymentDate().isBefore(LocalDate.now()) && request.firstPaymentDate() != transaction.getFirstPaymentDate()) {
            throw new IllegalArgumentException("Cannot change first payment date of transaction that has already started");
        }

        if (!request.currencyId().equals(transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));
            transaction.setCurrency(currency);
        }
        transaction.setName(request.name());
        transaction.setAmount(request.amount());;
        transaction.setNextPaymentDate(request.nextPaymentDate());
        transaction.setFinalPaymentDate(request.firstPaymentDate());
        transaction.setInterval(PeriodEnum.fromPeriod(Period.parse(request.interval())));
        transaction.setDescription(request.description());

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
}
