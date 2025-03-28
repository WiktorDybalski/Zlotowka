package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeTransactionService {
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Transactional
    public OneTimeTransaction createTransaction(OneTimeTransactionRequest request) {
        log.info("Creating transaction with request: {}", request);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.userId()));

        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));


        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .isIncome(request.isIncome())
                .date(request.date())
                .description(request.description())
                .build();

        if (request.date().isBefore(LocalDate.now())) {
            userService.updateBudget(request, transaction);
        }

        oneTimeTransactionRepository.save((transaction));
        log.info("Created new transaction successfully");

        return transaction;
    }

    public OneTimeTransaction getTransaction(Integer id) {
        return oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
    }

    @Transactional
    public OneTimeTransaction updateOneTimeTransaction(OneTimeTransactionRequest request) {
        log.info("Updating transaction with request: {}", request);
        OneTimeTransaction transaction = getTransaction(request.transactionId());

        if (request.date().isBefore(LocalDate.now()))
            updateTransactionBeforeCurrentTime(request, transaction);
        else {
            updateTransactionAfterCurrentTime(request, transaction);
        }
        if (!Objects.equals(request.currencyId(), transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));

            transaction.setCurrency(currency);
        }

        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setName(request.name());
        transaction.setIsIncome(request.isIncome());
        transaction.setDescription(request.description());

        oneTimeTransactionRepository.save((transaction));
        return transaction;
    }


    private void updateTransactionBeforeCurrentTime(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (transaction.getDate().isBefore(LocalDate.now())) {
            if (!request.amount().equals(transaction.getAmount()) || request.currencyId().equals(transaction.getCurrency().getCurrencyId())) {
                userService.updateBudget(request, transaction);
            }
        }
    }

    private void updateTransactionAfterCurrentTime(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (transaction.getDate().isAfter(LocalDate.now())) {
            if (!request.amount().equals(transaction.getAmount()) || request.currencyId().equals(transaction.getCurrency().getCurrencyId())) {
                userService.updateBudget(request, transaction);
            }
        }
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        log.info("Deleting transaction with Id {}", id);
        OneTimeTransaction transaction = this.getTransaction(id);

        if (transaction.getDate().isBefore(LocalDate.now())) {
            userService.updateBudget(transaction);
        }
        oneTimeTransactionRepository.delete(transaction);
    }
}
