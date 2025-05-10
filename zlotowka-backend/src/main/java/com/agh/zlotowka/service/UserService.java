package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.RegistrationRequest;
import com.agh.zlotowka.dto.UpdatePasswordRequest;
import com.agh.zlotowka.dto.UserDetailsRequest;
import com.agh.zlotowka.dto.UserResponse;
import com.agh.zlotowka.exception.CurrencyConversionException;
import com.agh.zlotowka.model.Currency;
import com.agh.zlotowka.model.User;
import com.agh.zlotowka.repository.CurrencyRepository;
import com.agh.zlotowka.repository.UserRepository;
import com.agh.zlotowka.security.CustomUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeCurrencies() {
        if (currencyRepository.findAll().isEmpty()) {
            currencyService.addCurrencies();
        }
    }

    @Transactional
    public User registerUser(RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with the given email address already exists");
        }

        Currency defaultCurrency = currencyRepository.findByIsoCode("PLN")
                .orElseThrow(() -> new EntityNotFoundException("Default currency PLN not found"));


        User newUser = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber("")
                .dateOfJoining(LocalDate.now())
                .currentBudget(new BigDecimal(0))
                .currency(defaultCurrency)
                .darkMode(false)
                .notificationsByEmail(false)
                .notificationsByPhone(false)
                .build();

        return userRepository.save(newUser);
    }

    public void removeTransactionAmountFromBudget(int currencyId, BigDecimal amount, boolean isIncome, User user) {
        BigDecimal budget = user.getCurrentBudget();
        BigDecimal amountInUserCurrency;

        Currency requestCurrency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new EntityNotFoundException("Currency service: Currency not found with ID: " + currencyId));

        try {
            if (user.getCurrency().getCurrencyId().equals(currencyId)) {
                amountInUserCurrency = amount;
            } else {
                amountInUserCurrency = currencyService.convertCurrency(amount, requestCurrency.getIsoCode(), user.getCurrency().getIsoCode());
            }
            addTransactionToBudget(user, budget, amountInUserCurrency, !isIncome);
        } catch (CurrencyConversionException e) {
            log.error("Currency conversion failed", e);
        }
    }

    public UserResponse getCurrentUser(CustomUserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfJoining(),
                user.getCurrentBudget(),
                user.getCurrency(),
                user.getDarkMode(),
                user.getNotificationsByEmail(),
                user.getNotificationsByPhone()
        );
    }


    public void addTransactionAmountToBudget(int currencyId, BigDecimal amount, boolean isIncome, User user) {
        BigDecimal budget = user.getCurrentBudget();
        BigDecimal amountInUserCurrency;

        Currency requestCurrency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new EntityNotFoundException("Currency service: Currency not found with ID: " + currencyId));

        try {
            if (user.getCurrency().equals(requestCurrency)) {
                amountInUserCurrency = amount;
            } else {
                amountInUserCurrency = currencyService.convertCurrency(amount, requestCurrency.getIsoCode(), user.getCurrency().getIsoCode());
            }
            addTransactionToBudget(user, budget, amountInUserCurrency, isIncome);
        } catch (CurrencyConversionException e) {
            log.error("Currency conversion failed", e);
        }
    }

    private void addTransactionToBudget(User user, BigDecimal budget, BigDecimal amount, boolean isAddTransaction) {
        if (isAddTransaction) {
            user.setCurrentBudget(budget.add(amount));
        } else {
            user.setCurrentBudget(budget.subtract(amount));
        }
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUserDetails(UserDetails userDetails, UserDetailsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.email() != null && !request.email().equals(user.getEmail()) &&
                userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.darkMode() != null) {
            user.setDarkMode(Boolean.parseBoolean(request.darkMode()));
        }

        if (request.notificationsByEmail() != null) {
            user.setNotificationsByEmail(Boolean.parseBoolean(request.notificationsByEmail()));
        }

        if (request.notificationsByPhone() != null) {
            user.setNotificationsByPhone(Boolean.parseBoolean(request.notificationsByPhone()));
        }

        User updatedUser = userRepository.save(user);
        return new UserResponse(
                updatedUser.getUserId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(),
                updatedUser.getDateOfJoining(),
                updatedUser.getCurrentBudget(),
                updatedUser.getCurrency(),
                updatedUser.getDarkMode(),
                updatedUser.getNotificationsByEmail(),
                updatedUser.getNotificationsByPhone()
        );
    }

    @Transactional
    public void updateUserPassword(UserDetails userDetails, UpdatePasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Password update request cannot be null");
        }

        if (request.newPassword() == null || request.oldPassword() == null || request.confirmNewPassword() == null) {
            throw new IllegalArgumentException("Passwords cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user ID: {}", user.getUserId());
    }
}
