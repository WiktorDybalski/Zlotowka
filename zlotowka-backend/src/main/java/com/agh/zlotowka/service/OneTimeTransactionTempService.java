package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeTransactionTempService {

    @Transactional
    public OneTimeTransaction createTransaction(OneTimeTransactionRequest request) {
        log.info("Creating transaction with request: {}", request);

        User user = User.builder()
                .userId(request.userId())
                .build();

        Currency currency = Currency.builder()
                .currencyId(request.currencyId())
                .isoCode("PLN")
                .build();


        return OneTimeTransaction.builder()
                .user(user)
                .name(request.name())
                .amount(request.amount())
                .currency(currency)
                .isIncome(request.isIncome())
                .date(request.date())
                .description(request.description())
                .build();
    }

    public OneTimeTransaction getTransaction(Integer id) {

        User user = User.builder()
                .userId(1)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        return OneTimeTransaction.builder()
                .transactionId(id)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(1000))
                .currency(currency)
                .isIncome(false)
                .date(LocalDate.of(2023, 12, 15))
                .description("Kamilek kupił kebaba w hamisie i był dobry")
                .build();
    }

    @Transactional
    public OneTimeTransaction updateOneTimeTransaction(OneTimeTransactionRequest request) {
        log.info("Updating transaction with request: {}", request);

        User user = User.builder()
                .userId(1)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        return OneTimeTransaction.builder()
                .transactionId(1)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(1000))
                .currency(currency)
                .isIncome(false)
                .date(LocalDate.of(2023, 12, 15))
                .description("Kamilek kupił kebaba w hamisie i był dobry")
                .build();
    }

    @Transactional
    public void deleteTransaction(Integer id) {

    }

    public List<OneTimeTransaction> getAllTransactions(int userId) {

        User user = User.builder()
                .userId(userId)
                .build();

        Currency currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();

        OneTimeTransaction transaction1 = OneTimeTransaction.builder()
                .transactionId(1)
                .user(user)
                .name("Tranzakcja 1")
                .amount(new BigDecimal(3000))
                .currency(currency)
                .isIncome(false)
                .date(LocalDate.of(2023, 11, 10))
                .description("Rachunki domowe")
                .build();

        OneTimeTransaction transaction2 = OneTimeTransaction.builder()
                .transactionId(2)
                .user(user)
                .name("Przykładowa transakcja")
                .amount(new BigDecimal(1000))
                .currency(currency)
                .isIncome(false)
                .date(LocalDate.of(2023, 12, 15))
                .description("Kamilek kupił kebaba w hamisie i był dobry")
                .build();

        return List.of(transaction1, transaction2);
    }
}
