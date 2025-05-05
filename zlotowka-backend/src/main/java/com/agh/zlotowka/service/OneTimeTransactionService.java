package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.dto.PaginatedTransactionsDTO;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeTransactionService {
    private final OneTimeTransactionRepository oneTimeTransactionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Transactional
    public OneTimeTransactionDTO createTransaction(OneTimeTransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", request.userId())));

        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

        if (!request.date().isAfter(LocalDate.now())) {
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

        return getOneTimeTransactionDTO(transaction);
    }

    public OneTimeTransactionDTO getTransaction(Integer id) {
        return getOneTimeTransactionDTO(oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", id))));
    }

    @Transactional
    public OneTimeTransactionDTO updateOneTimeTransaction(OneTimeTransactionRequest request, int transactionId) {
        log.info("Updating transaction with request: {}", request);
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", transactionId)));

        validateTransactionOwnership(request.userId(), transaction.getUser().getUserId());

        if (!request.date().isAfter(LocalDate.now()))
            updateTransactionBeforeCurrentTime(request, transaction);
        else {
            updateTransactionAfterCurrentTime(transaction);
        }
        return updateTransaction(request, transaction);
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        log.info("Deleting transaction with Id {}", id);
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Transaction with Id %d not found", id)));

        if (!transaction.getDate().isAfter(LocalDate.now())) {
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
        oneTimeTransactionRepository.delete(transaction);
    }

    private List<OneTimeTransaction> getAllTransactionsByUserId(Integer userId) {
        List<OneTimeTransaction> allTransactions = oneTimeTransactionRepository.findAllByUser(userId, LocalDate.now());

        if (allTransactions.isEmpty())
            userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with Id %d not found", userId)));

        return allTransactions;
    }

    public void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied");
        }
    }

    private OneTimeTransactionDTO getOneTimeTransactionDTO(OneTimeTransaction transaction) {
        return OneTimeTransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .name(transaction.getName())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .isIncome(transaction.getIsIncome())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .build();
    }

    private void validateTransactionOwnership(Integer requestSenderId, Integer transactionOwner) {
        if (!requestSenderId.equals(transactionOwner))
            throw new IllegalArgumentException(String.format("User Id %d does not match the transaction owner", requestSenderId));
    }

    private void updateTransactionBeforeCurrentTime(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (!transaction.getDate().isAfter(LocalDate.now())) {
            if ((!request.amount().equals(transaction.getAmount()) || request.currencyId().equals(transaction.getCurrency().getCurrencyId()))) {
                userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), request.isIncome(), transaction.getUser());
                userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), transaction.getUser());
            }
        } else {
            userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), transaction.getUser());
        }
    }

    private void updateTransactionAfterCurrentTime(OneTimeTransaction transaction) {
        if (!transaction.getDate().isAfter(LocalDate.now())) {
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }
    }
    public OneTimeTransactionDTO getTransactionWithUserCheck(Integer transactionId, CustomUserDetails userDetails) {
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied to this transaction");
        }

        return getOneTimeTransactionDTO(transaction);
    }

    @Transactional
    public void deleteTransactionWithUserCheck(Integer transactionId, CustomUserDetails userDetails) {
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Access denied to this transaction");
        }

        if (transaction.getDate().isBefore(LocalDate.now())) {
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(),
                    transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
        }

        oneTimeTransactionRepository.delete(transaction);
    }

    private OneTimeTransactionDTO updateTransaction(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (!Objects.equals(request.currencyId(), transaction.getCurrency().getCurrencyId())) {
            Currency currency = currencyRepository.findById(request.currencyId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Currency with Id %d not found", request.currencyId())));

            transaction.setCurrency(currency);
        }

        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setName(request.name());
        transaction.setIsIncome(request.isIncome());
        transaction.setDescription(request.description());

        oneTimeTransactionRepository.save(transaction);
        return getOneTimeTransactionDTO(transaction);
    }
}
