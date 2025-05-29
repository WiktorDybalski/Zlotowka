package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneTimeTransactionServiceUnitTest {

    @InjectMocks
    private OneTimeTransactionService service;

    @Mock
    private OneTimeTransactionRepository transactionRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrencyRepository currencyRepository;

    private final LocalDate today = LocalDate.now();

    @Test
    void createTransactionTest() {
        User user = new User();
        user.setUserId(1);
        Currency currency = new Currency();
        currency.setCurrencyId(2);

        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                1, "Lunch", new BigDecimal(50), 2, true, today, "Opis");

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(user)
                .currency(currency)
                .name("Lunch")
                .amount(new BigDecimal(50))
                .isIncome(true)
                .date(today)
                .description("Description")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));
        when(transactionRepository.save(any())).thenReturn(transaction);

        OneTimeTransactionDTO result = service.createTransaction(request);

        assertEquals("Lunch", result.name());
        assertEquals(new BigDecimal(50), result.amount());
        verify(userService).addTransactionAmountToBudget(2, new BigDecimal(50), true, user);
        verify(transactionRepository).save(any());
    }

    @Test
    void getTransactionTest() {
        User user = new User();
        user.setUserId(1);
        Currency currency = new Currency();
        currency.setCurrencyId(2);

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .transactionId(1)
                .user(user)
                .currency(currency)
                .name("Shopping")
                .amount(new BigDecimal(100))
                .isIncome(false)
                .date(LocalDate.now())
                .description("Description")
                .build();

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        OneTimeTransactionDTO result = service.getTransaction(1);

        assertEquals("Shopping", result.name());
        assertEquals(new BigDecimal(100), result.amount());
    }

    @Test
    void deleteTransactionTest() {
        User user = new User();
        user.setUserId(1);
        Currency currency = new Currency();
        currency.setCurrencyId(2);
        LocalDate pastDate = LocalDate.now().minusDays(1);

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .transactionId(1)
                .user(user)
                .currency(currency)
                .amount(new BigDecimal(100))
                .isIncome(true)
                .date(pastDate)
                .build();

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        service.deleteTransaction(1);

        verify(userService).removeTransactionAmountFromBudget(2, new BigDecimal(100), true, user);
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void updateOneTimeTransactionTest() {
        User user = new User();
        user.setUserId(1);
        Currency oldCurrency = new Currency();
        oldCurrency.setCurrencyId(2);
        Currency newCurrency = new Currency();
        newCurrency.setCurrencyId(3);

        OneTimeTransaction transaction = OneTimeTransaction.builder()
                .user(user)
                .currency(oldCurrency)
                .name("Lunch")
                .amount(new BigDecimal(50))
                .isIncome(true)
                .date(today)
                .description("Description")
                .build();

        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                1, "New Name", new BigDecimal(150), 3, true, LocalDate.now().minusDays(1), "New Description"
        );

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
        when(currencyRepository.findById(3)).thenReturn(Optional.of(newCurrency));

        OneTimeTransactionDTO result = service.updateOneTimeTransaction(request, 1);

        assertEquals("New Name", result.name());
        assertEquals(new BigDecimal(150), result.amount());
        assertEquals("New Description", result.description());
    }
}
