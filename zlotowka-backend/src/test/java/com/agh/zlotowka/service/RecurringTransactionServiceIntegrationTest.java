package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RecurringTransactionDTO;
import com.agh.zlotowka.dto.RecurringTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.OneTimeTransaction;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.RecurringTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class RecurringTransactionServiceIntegrationTest {

    @Autowired
    private RecurringTransactionService service;

    @Autowired
    private RecurringTransactionRepository transactionRepository;

    @Autowired
    private OneTimeTransactionRepository oneTimeTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private User user;
    private Currency currency;
    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setup() {
        currency = Currency.builder()
                .currencyId(1)
                .isoCode("PLN")
                .build();
        currency = currencyRepository.save(currency);

        user = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phoneNumber("+9876543210")
                .password("strongPassword")
                .dateOfJoining(LocalDate.now())
                .currentBudget(new BigDecimal("500.00").setScale(2, RoundingMode.HALF_UP))
                .currency(currency)
                .build();
        user = userRepository.save(user);
    }

    @Test
    void createAndGetRecurringTransactionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Subscription", new BigDecimal(100), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(6), "Netflix");

        RecurringTransactionDTO created = service.createTransaction(request);
        RecurringTransactionDTO fetched = service.getTransaction(created.transactionId());

        assertEquals("Subscription", fetched.name());
        assertEquals(user.getUserId(), fetched.userId());
    }

    @Test
    void createTransactionWithInvalidUserThrowsExceptionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                9999, "Ghost", new BigDecimal(10), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(1), "Invalid user");

        assertThrows(EntityNotFoundException.class, () -> service.createTransaction(request));
    }

    @Test
    void createPastStartedRecurringTransactionUpdatesBudgetAndCreatesOneTimeTransactions() {
        LocalDate firstPayment = today.minusMonths(3);
        LocalDate lastPayment = today.plusMonths(3);
        BigDecimal amount = new BigDecimal("50.00");
        boolean isIncome = false;

        BigDecimal budgetBefore = userRepository.findById(user.getUserId()).orElseThrow().getCurrentBudget();

        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Rent", amount, currency.getCurrencyId(), isIncome,
                "P1M", firstPayment, lastPayment, "Monthly rent");

        RecurringTransactionDTO created = service.createTransaction(request);

        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        BigDecimal expectedChange = amount.multiply(new BigDecimal(4));
        BigDecimal expectedBudget = budgetBefore.subtract(expectedChange);

        assertEquals(expectedBudget, updatedUser.getCurrentBudget(), "Budget should be updated correctly");

        List<OneTimeTransaction> oneTimeTransactions = oneTimeTransactionRepository
                .findAll()
                .stream()
                .filter(tx -> tx.getName().equals("Rent") && tx.getUser().getUserId().equals(user.getUserId()))
                .toList();

        assertEquals(4, oneTimeTransactions.size(), "There should be 4 overdue OneTimeTransactions created");
    }

    @Test
    void getRecurringTransactionNotFoundThrowsExceptionTest() {
        int nonExistentId = 99999;

        var exception = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> service.getTransaction(nonExistentId)
        );

        assertEquals("Nie znaleziono transakcji o ID " + nonExistentId, exception.getMessage());
    }

    @Test
    void updateRecurringTransactionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Gym", new BigDecimal(120), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(3), "Gym monthly");

        RecurringTransactionDTO created = service.createTransaction(request);

        RecurringTransactionRequest updateRequest = new RecurringTransactionRequest(
                user.getUserId(), "Gym Plus", new BigDecimal(150), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(6), "Updated Gym");

        RecurringTransactionDTO updated = service.updateTransaction(updateRequest, created.transactionId());

        assertEquals("Gym Plus", updated.name());
        assertEquals(new BigDecimal(150), updated.amount());
        assertEquals("Updated Gym", updated.description());
    }

    @Test
    void updateTransactionWithInvalidUserThrowsExceptionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Spotify", new BigDecimal(40), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(2), "Music");

        RecurringTransactionDTO created = service.createTransaction(request);

        int fakeUserId = user.getUserId() + 1;

        RecurringTransactionRequest updateRequest = new RecurringTransactionRequest(
                fakeUserId, "Spotify Premium", new BigDecimal(50), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(3), "Upgraded");

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateTransaction(updateRequest, created.transactionId()));

        assertTrue(exception.getMessage().contains("ID użytkownika"));
    }

    @Test
    void createTransactionWithInvalidDatesThrowsExceptionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Wrong dates", new BigDecimal(20), currency.getCurrencyId(), false,
                "P1M", today.plusMonths(2), today, "Invalid range");

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.createTransaction(request));

        assertEquals("Data pierwszej płatności musi być przed datą ostatniej płatności", exception.getMessage());
    }

    @Test
    void updateTransactionWithChangedTypeThrowsExceptionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Transfer", new BigDecimal(200), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(2), "Bank");

        RecurringTransactionDTO created = service.createTransaction(request);

        RecurringTransactionRequest updateRequest = new RecurringTransactionRequest(
                user.getUserId(), "Transfer Income", new BigDecimal(200), currency.getCurrencyId(), true,
                "P1M", today, today.plusMonths(2), "Now income");

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateTransaction(updateRequest, created.transactionId()));

        assertEquals("Nie można zmienić typu transakcji", exception.getMessage());
    }

    @Test
    void deleteRecurringTransactionTest() {
        RecurringTransactionRequest request = new RecurringTransactionRequest(
                user.getUserId(), "Apple Music", new BigDecimal(30), currency.getCurrencyId(), false,
                "P1M", today, today.plusMonths(12), "Music sub");

        RecurringTransactionDTO created = service.createTransaction(request);

        service.deleteTransaction(created.transactionId());

        var exception = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> service.getTransaction(created.transactionId())
        );

        assertEquals("Nie znaleziono transakcji o ID " + created.transactionId(), exception.getMessage());
    }

    @Test
    void deleteNonExistentTransactionThrowsExceptionTest() {
        int nonExistentId = 123456;

        var exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteTransaction(nonExistentId));

        assertEquals("Nie znaleziono transakcji o ID " + nonExistentId, exception.getMessage());
    }
}
