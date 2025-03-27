package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class RecurringTransactionTempService {

    @Transactional
    public RecurringTransaction createTransaction(RecurringTransactionRequest request) {
        log.info("Creating transaction with request: {}", request);

        User user = User.builder()
                .userId(request.userId())
                .build();

        Currency currency = Currency.builder()
                .currencyId(request.currencyId())
                .isoCode("PLN")
                .build();


        return RecurringTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .income(request.income())
                .startPaymentDate(request.firstPaymentDate())
                .lastPaymentDate(request.lastPaymentDate())
                .description(request.description())
                .build();
    }

    public RecurringTransaction getTransaction(Integer id) {

        User user = User.builder()
                .userId(1)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        return RecurringTransaction.builder()
                .transactionId(id)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(1000))
                .currency(currency)
                .income(false)
                .startPaymentDate(LocalDate.of(2023, 11, 10))
                .lastPaymentDate(LocalDate.of(2023, 12, 15))
                .description("Kamilek kupił kebaba w hamisie i był dobry")
                .build();
    }

    @Transactional
    public RecurringTransaction updateTransaction(RecurringTransactionRequest request) {
        log.info("Updating transaction with request: {}", request);

        User user = User.builder()
                .userId(1)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        return RecurringTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .income(request.income())
                .startPaymentDate(request.firstPaymentDate())
                .interval(Duration.from(request.interval()))
                .lastPaymentDate(request.lastPaymentDate())
                .description(request.description())
                .build();
    }

    @Transactional
    public void deleteTransaction(Integer id) {

    }

    public List<RecurringTransaction> getAllTransactions(int userId) {

        User user = User.builder()
                .userId(userId)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        RecurringTransaction transaction1 = RecurringTransaction.builder()
                .transactionId(1)
                .user(user)
                .name("Transakcja 1")
                .amount(new BigDecimal(3000))
                .currency(currency)
                .income(false)
                .description("Rachunki domowe")
                .build();

        RecurringTransaction transaction2 = RecurringTransaction.builder()
                .transactionId(2)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(1000))
                .currency(currency)
                .income(false)
                .description("Kamilek kupił kebaba w hamisie i był dobry2")
                .build();

        return List.of(transaction1, transaction2);
    }
}
