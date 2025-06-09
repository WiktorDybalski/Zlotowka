package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionDTO;
import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.OneTimeTransactionRepository;
import com.agh.zlotowka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OneTimeTransactionServiceIntegrationTest {

    @Autowired
    private OneTimeTransactionService service;

    @Autowired
    private OneTimeTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserService userService;

    private User user;
    private Currency currency;

    private final LocalDate today = LocalDate.now();


    @BeforeEach
    void setup() {
        currency = Currency.builder()
                .isoCode("PLN")
                .build();
        currency = currencyRepository.save(currency);

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .password("securePassword123")
                .dateOfJoining(LocalDate.now())
                .currentBudget(new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP))
                .currency(currency)
                .build();
        user = userRepository.save(user);
    }

    @Test
    void createAndGetTransactionTest() {
        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                user.getUserId(), "Lunch", new BigDecimal(50), 2, true, today, "Description");

        OneTimeTransactionDTO created = service.createTransaction(request);
        OneTimeTransactionDTO fetched = service.getTransaction(created.transactionId());

        assertEquals("Lunch", fetched.name());
        assertEquals(user.getUserId(), fetched.userId());
    }

    @Test
    void createPastTransactionTest() {
        BigDecimal initialBudget = user.getCurrentBudget();

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BigDecimal transactionAmount = new BigDecimal(150);

        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                user.getUserId(),
                "Income",
                transactionAmount,
                currency.getCurrencyId(),
                true,
                yesterday,
                "Example imcome"
        );

        service.createTransaction(request);

        User updatedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("Użytkownik nie istnieje"));

        BigDecimal expected = initialBudget.add(transactionAmount);
        assertEquals(expected, updatedUser.getCurrentBudget());
    }


    @Test
    void updateTransactionTest() {

        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                user.getUserId(), "Lunch", new BigDecimal(50), currency.getCurrencyId(), true, today, "Description");

        OneTimeTransactionDTO created = service.createTransaction(request);

        OneTimeTransactionRequest updateRequest = new OneTimeTransactionRequest(
                user.getUserId(), "Updated Lunch", new BigDecimal(75), currency.getCurrencyId(), false, today, "Updated description");

        OneTimeTransactionDTO updated = service.updateOneTimeTransaction(updateRequest, created.transactionId());

        assertEquals("Updated Lunch", updated.name());
        assertEquals(new BigDecimal(75), updated.amount());
        assertEquals("Updated description", updated.description());
        assertEquals(false, updated.isIncome());
    }

    @Test
    void updatePastTransactionTest() {
        BigDecimal initialBudget = user.getCurrentBudget();

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BigDecimal initialTransactionAmount = new BigDecimal(150);
        BigDecimal updatedTransactionAmount = new BigDecimal(100);

        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                user.getUserId(),
                "Income",
                initialTransactionAmount,
                currency.getCurrencyId(),
                true,
                yesterday,
                "Example imcome"
        );

        OneTimeTransactionDTO created = service.createTransaction(request);

        OneTimeTransactionRequest updateRequest = new OneTimeTransactionRequest(
                user.getUserId(), "Updated income", updatedTransactionAmount, currency.getCurrencyId(), true, yesterday, "Updated income");

        OneTimeTransactionDTO updated = service.updateOneTimeTransaction(updateRequest, created.transactionId());

        User userAfterUpdate = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("Użytkownik nie istnieje"));

        BigDecimal expectedBalance = initialBudget.add(updatedTransactionAmount);

        assertEquals("Updated income", updated.name());
        assertEquals(updatedTransactionAmount, updated.amount());
        assertEquals("Updated income", updated.description());
        assertEquals(true, updated.isIncome());
        assertEquals(expectedBalance , userAfterUpdate.getCurrentBudget());
    }

    @Test
    void deleteTransactionTest() {
        OneTimeTransactionRequest request = new OneTimeTransactionRequest(
                user.getUserId(), "Dinner", new BigDecimal(40), currency.getCurrencyId(), false, today, "Description");

        OneTimeTransactionDTO created = service.createTransaction(request);

        service.deleteTransaction(created.transactionId());

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> service.getTransaction(created.transactionId())
        );

        assertEquals("Nie znaleziono transakcji o ID " + created.transactionId(), exception.getMessage());
    }


    @Test
    void getTransactionNotFoundThrowsExceptionTest() {
        int nonExistentId = 99999;

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> service.getTransaction(nonExistentId)
        );

        assertEquals("Nie znaleziono transakcji o ID " + nonExistentId, exception.getMessage());
    }

}

