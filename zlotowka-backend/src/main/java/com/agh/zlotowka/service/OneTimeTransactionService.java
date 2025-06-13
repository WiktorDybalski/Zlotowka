package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.SubPlanRepository;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SubPlanRepository subplanRepository;
    private final SystemNotificationService systemNotificationService;

    @Transactional
    public OneTimeTransactionDTO createTransaction(OneTimeTransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono użytkownika o ID %d", request.userId())));

        Currency currency = currencyRepository.findById(request.currencyId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono waluty o ID %d", request.currencyId())));

        if (!request.date().isAfter(LocalDate.now())) {
            BigDecimal budgetBefore = user.getCurrentBudget();
            userService.addTransactionAmountToBudget(request.currencyId(), request.amount(), request.isIncome(), user);
            BigDecimal budgetAfter = user.getCurrentBudget();
            if (budgetBefore.compareTo(BigDecimal.ZERO) >= 0 &&
                    budgetAfter.compareTo(BigDecimal.ZERO) < 0) {
                systemNotificationService.checkUserBalanceAndSendWarning(user);
            }
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
        return getOneTimeTransactionDTO(transaction);
    }

    public OneTimeTransactionDTO getTransaction(Integer id) {
        return getOneTimeTransactionDTO(oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono transakcji o ID %d", id))));
    }

    @Transactional
    public OneTimeTransactionDTO updateOneTimeTransaction(OneTimeTransactionRequest request, int transactionId) {
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono transakcji o ID %d", transactionId)));

        validateTransactionOwnership(request.userId(), transaction.getUser().getUserId());

        if (!request.date().isAfter(LocalDate.now())) {
            User user = transaction.getUser();
            BigDecimal budgetBefore = user.getCurrentBudget();
            updateTransactionBeforeCurrentTime(request, transaction);
            BigDecimal budgetAfter = user.getCurrentBudget();
            if (budgetBefore.compareTo(BigDecimal.ZERO) >= 0 &&
                    budgetAfter.compareTo(BigDecimal.ZERO) < 0) {
                systemNotificationService.checkUserBalanceAndSendWarning(user);
            }
        }
        else {
            updateTransactionAfterCurrentTime(transaction);
        }
        return updateTransaction(request, transaction);
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono transakcji o ID %d", id)));

        if (!transaction.getDate().isAfter(LocalDate.now())) {
            User user = transaction.getUser();
            BigDecimal budgetBefore = user.getCurrentBudget();
            userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
            BigDecimal budgetAfter = user.getCurrentBudget();
            if (budgetBefore.compareTo(BigDecimal.ZERO) >= 0 &&
                    budgetAfter.compareTo(BigDecimal.ZERO) < 0) {
                systemNotificationService.checkUserBalanceAndSendWarning(user);
            }
        }

        Subplan subplan = subplanRepository.findByTransactionId(id);
        if (subplan != null) {
            subplan.setTransaction(null);
            subplanRepository.save(subplan);
        }

        oneTimeTransactionRepository.delete(transaction);
    }

    private List<OneTimeTransaction> getAllTransactionsByUserId(Integer userId) {
        List<OneTimeTransaction> allTransactions = oneTimeTransactionRepository.findAllByUser(userId, LocalDate.now());

        if (allTransactions.isEmpty())
            userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono transakcji o ID %d", userId)));

        return allTransactions;
    }

    public void validateUserId(Integer userId, CustomUserDetails userDetails) {
        if (!userId.equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Dostęp zabroniony");
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
            throw new IllegalArgumentException(String.format("ID użytkownika %d nie odpowiada właścicielowi transakcji", requestSenderId));
    }

    private void updateTransactionBeforeCurrentTime(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
        if (!transaction.getDate().isAfter(LocalDate.now())) {
            if ((!request.amount().equals(transaction.getAmount()) || request.currencyId().equals(transaction.getCurrency().getCurrencyId())) || request.isIncome() != transaction.getIsIncome()) {
                userService.removeTransactionAmountFromBudget(transaction.getCurrency().getCurrencyId(), transaction.getAmount(), transaction.getIsIncome(), transaction.getUser());
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
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji"));

        if (!transaction.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Brak dostępu do tej transakcji");
        }

        return getOneTimeTransactionDTO(transaction);
    }

    @Transactional
    public void deleteTransactionWithUserCheck(Integer transactionId, CustomUserDetails userDetails) {
        OneTimeTransaction transaction = oneTimeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji"));

        if (!transaction.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new IllegalArgumentException("Brak dostępu do tej transakcji");
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
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Nie znaleziono waluty o ID %d", request.currencyId())));

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
