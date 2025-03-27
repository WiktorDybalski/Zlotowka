package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.RecurringTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;

    public RecurringTransaction createTransaction(RecurringTransactionRequest request) {
        // TODO: find user in database
        User user = User.builder()
                .userId(request.userId())
                .build();

        // TODO: find currency in database
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
                .description(request.description())
                .build();
    }

    public RecurringTransaction getTransaction(Integer id) {
        // TODO: find user in database
        User user = User.builder()
                .userId(1)
                .build();

        // TODO: find currency in database
        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();


        return RecurringTransaction.builder()
                .transactionId(1)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(2000))
                .currency(currency)
                .income(false)
                .startPaymentDate(LocalDate.of(2023, 12, 15))
                .interval(Duration.from(Period.ofDays(30)))
                .lastPaymentDate(LocalDate.of(2025, 12, 15))
                .description("Kamilek kupił kebaba w hamisie i był dobry")
                .build();
    }

    public void updateTransaction(Integer id) {
    }

    public void deleteTransaction(Integer id) {
    }
}
