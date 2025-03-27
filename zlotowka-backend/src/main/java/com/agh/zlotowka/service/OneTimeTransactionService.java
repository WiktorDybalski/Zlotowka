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

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeTransactionService {
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
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
                .income(request.income())
                .date(request.date())
                .description(request.description())
                .build();

//        TODO: Update user budget if transaction was in the past
//        if (request.date().isBefore(LocalDate.now())) {
//
//        }

        oneTimeTransactionRepository.save((transaction));
        log.info("Created new transaction successfully");

        return transaction;
    }

    public OneTimeTransaction getTransaction(Integer id) {
        return oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
    }

    @Transactional
    public OneTimeTransaction updateTransaction(OneTimeTransactionRequest request) {
        log.info("Updating transaction with request: {}", request);

        OneTimeTransaction transaction = this.getTransaction(request.transactionId());

        if (!Objects.equals(request.currencyId(), transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + request.currencyId()));


            transaction.setCurrency(currency);
        }

//        TODO: Update user budget if new date is before current time
//        if (request.date().isBefore(LocalDate.now()) && transaction.getDate().isAfter(LocalDate.now())) {
//
//        }

        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setName(request.name());
        transaction.setIncome(request.income());
        transaction.setDescription(request.description());

        oneTimeTransactionRepository.save((transaction));
        log.info("Updated existing transactions with id {}", request.transactionId());

        return transaction;
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        OneTimeTransaction transaction = this.getTransaction(id);

        oneTimeTransactionRepository.delete(transaction);
        log.info("Transaction with Id {} has been deleted", id);
    }
}
