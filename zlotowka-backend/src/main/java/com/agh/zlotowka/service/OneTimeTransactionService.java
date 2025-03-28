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
import java.util.List;
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

        if (request.date().isBefore(LocalDate.now())) {
            userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), user);
        }
        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .isIncome(request.isIncome())
                .date(request.date())
                .description(request.description())
                .build();

        oneTimeTransactionRepository.save((transaction));
        log.info("Created new transaction successfully");

        return transaction;
    }

    public OneTimeTransaction getTransaction(Integer id) {
        return oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
    }

    @Transactional
    public OneTimeTransaction updateOneTimeTransaction(OneTimeTransactionRequest request, int transactionId) {
        log.info("Updating transaction with request: {}", request);
        OneTimeTransaction transaction = getTransaction(transactionId);

        if (request.date().isBefore(LocalDate.now()))
            updateTransactionBeforeCurrentTime(request, transaction);
        else {
            updateTransactionAfterCurrentTime(transaction);
        }
        return updateTransaction(request, transaction);
    }

    private void updateTransactionBeforeCurrentTime(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (transaction.getDate().isBefore(LocalDate.now())) {
            if ((!request.amount().equals(transaction.getAmount()) || request.currencyId().equals(transaction.getCurrency().getCurrencyId()))) {
                userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), request.isIncome(), transaction.getUser());
                userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), transaction.getUser());
            }
        } else {
            userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), transaction.getUser());
        }
    }

    private void updateTransactionAfterCurrentTime(OneTimeTransaction transaction) {
        if (transaction.getDate().isBefore(LocalDate.now())) {
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
    }

    OneTimeTransaction updateTransaction(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
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

        oneTimeTransactionRepository.save(transaction);
        return transaction;
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        log.info("Deleting transaction with Id {}", id);
        OneTimeTransaction transaction = this.getTransaction(id);

        if (transaction.getDate().isBefore(LocalDate.now())) {
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
        oneTimeTransactionRepository.delete(transaction);
    }

    public List<OneTimeTransaction> getAllTransactionsByUserId(Integer userId) {
        return oneTimeTransactionRepository.findAllByUser(userId);
    }
}