package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.*;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceUnitTest {

    @InjectMocks
    private RecurringTransactionService service;

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;
    @Mock
    private OneTimeTransactionRepository oneTimeTransactionRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrencyRepository currencyRepository;

    private User user;
    private Currency currency;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        currency = new Currency();
        currency.setCurrencyId(2);
    }

    @Test
    void createTransactionWithFutureDateTest() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1, "Future Salary", new BigDecimal("5000.00"), 2, true,
                "P1M", futureDate, futureDate.plusMonths(6), "Monthly salary");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));

        RecurringTransactionDTO result = service.createTransaction(request);

        assertEquals("Future Salary", result.name());
        assertEquals(new BigDecimal("5000.00"), result.amount());
        verify(recurringTransactionRepository).save(any());
    }

    @Test
    void createTransactionWithPastDateUpdatesBudgetTest() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1, "Old Subscription", new BigDecimal("20.00"), 2, false,
                "P1M", pastDate, LocalDate.now(), "Spotify");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));

        RecurringTransactionDTO result = service.createTransaction(request);

        assertEquals("Old Subscription", result.name());
        verify(userService).addTransactionAmountToBudget(eq(2), any(BigDecimal.class), eq(false), eq(user));
        verify(oneTimeTransactionRepository).saveAll(any());
    }

    @Test
    void createTransactionThrowsIfUserNotFoundTest() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1, "Invalid", BigDecimal.TEN, 2, true,
                "P1D", LocalDate.now(), LocalDate.now().plusDays(1), "Bad user");

        Exception exception = assertThrows(RuntimeException.class, () -> service.createTransaction(request));
        assertTrue(exception.getMessage().contains("Nie znaleziono użytkownika"));
    }

    @Test
    void createTransactionThrowsIfCurrencyNotFoundTest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.empty());

        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1, "Invalid", BigDecimal.TEN, 2, true,
                "P1D", LocalDate.now(), LocalDate.now().plusDays(1), "Bad currency");

        Exception exception = assertThrows(RuntimeException.class, () -> service.createTransaction(request));
        assertTrue(exception.getMessage().contains("Nie znaleziono waluty"));
    }

    @Test
    void createTransactionThrowsIfFirstDateAfterFinalTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1, "Invalid Date", BigDecimal.TEN, 2, true,
                "P1D", LocalDate.now().plusDays(10), LocalDate.now(), "Date error");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(request));
        assertEquals("Data pierwszej płatności musi być przed datą ostatniej płatności", exception.getMessage());
    }

    @Test
    void updateTransactionTest() {
        User user = new User();
        user.setUserId(1);

        Currency currency = new Currency();
        currency.setCurrencyId(1);

        RecurringTransaction existingTransaction = RecurringTransaction.builder()
                .transactionId(10)
                .user(user)
                .name("Old Name")
                .amount(new BigDecimal(100))
                .currency(currency)
                .isIncome(true)
                .interval(PeriodEnum.MONTHLY)
                .firstPaymentDate(LocalDate.of(2024, 1, 1))
                .finalPaymentDate(LocalDate.of(2026, 1, 1))
                .description("Old Desc")
                .build();

        RecurringTransactionRequest request = new RecurringTransactionRequest(
                1,
                "Updated Name",
                new BigDecimal(200),
                2,
                true,
                "P1M",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 9, 1),
                "Updated Desc"
        );

        when(recurringTransactionRepository.findById(10)).thenReturn(Optional.of(existingTransaction));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));

        RecurringTransactionDTO result = service.updateTransaction(request, 10);

        assertEquals("Updated Name", result.name());
        assertEquals(new BigDecimal(200), result.amount());
        assertEquals("Updated Desc", result.description());
        assertEquals(LocalDate.of(2025, 9, 1), result.finalPaymentDate());
        verify(recurringTransactionRepository).save(existingTransaction);
    }

    @Test
    void deleteTransactionTest() {
        User user = new User();
        user.setUserId(1);

        RecurringTransaction transaction = RecurringTransaction.builder()
                .transactionId(5)
                .user(user)
                .name("To be deleted")
                .build();

        when(recurringTransactionRepository.findById(5)).thenReturn(Optional.of(transaction));

        service.deleteTransaction(5);

        verify(recurringTransactionRepository).delete(transaction);
    }

}
