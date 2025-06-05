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
    private final EmailService emailService;

    @PostConstruct
    public void initializeCurrencies() {
        if (currencyRepository.findAll().isEmpty()) {
            currencyService.addCurrencies();
        }
    }

    @Transactional
    public User registerUser(RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem e-mail już istnieje");
        }

        Currency defaultCurrency = currencyRepository.findByIsoCode("PLN")
                .orElseThrow(() -> new EntityNotFoundException("Domyślna waluta PLN nie została znaleziona"));

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
                .orElseThrow(() -> new EntityNotFoundException("Usługa walutowa: Waluta o ID: " + currencyId + " nie została znaleziona"));

        try {
            if (user.getCurrency().getCurrencyId().equals(currencyId)) {
                amountInUserCurrency = amount;
            } else {
                amountInUserCurrency = currencyService.convertCurrency(amount, requestCurrency.getIsoCode(), user.getCurrency().getIsoCode());
            }
            addTransactionToBudget(user, budget, amountInUserCurrency, !isIncome);
        } catch (CurrencyConversionException e) {
            log.error("Konwersja waluty nie powiodła się", e);
        }
    }

    public UserResponse getCurrentUser(CustomUserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie został znaleziony"));

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
                .orElseThrow(() -> new EntityNotFoundException("Usługa walutowa: Waluta o ID: " + currencyId + " nie została znaleziona"));

        try {
            if (user.getCurrency().equals(requestCurrency)) {
                amountInUserCurrency = amount;
            } else {
                amountInUserCurrency = currencyService.convertCurrency(amount, requestCurrency.getIsoCode(), user.getCurrency().getIsoCode());
            }
            addTransactionToBudget(user, budget, amountInUserCurrency, isIncome);
        } catch (CurrencyConversionException e) {
            log.error("Konwersja waluty nie powiodła się", e);
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
            throw new IllegalArgumentException("Żądanie aktualizacji nie może być puste");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie został znaleziony"));

        if (request.email() != null) {
            validateAndUpdateEmail(request, user);
        }

        if (request.firstName() != null) {
            validateAndUpdateName("First", request.firstName(), user);
        }

        if (request.lastName() != null) {
            validateAndUpdateName("Last", request.lastName(), user);
        }

        if (request.phoneNumber() != null) {
            if (!request.phoneNumber().matches("^\\d{9}$")) {
                throw new IllegalArgumentException("Numer telefonu musi składać się dokładnie z 9 cyfr");
            }
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.darkMode() != null) {
            if (!request.darkMode().matches("^(true|false)$")) {
                throw new IllegalArgumentException("Wartość trybu ciemnego musi być 'true' lub 'false'");
            }
            user.setDarkMode(Boolean.parseBoolean(request.darkMode()));
        }

        if (request.notificationsByEmail() != null) {
            if (!request.notificationsByEmail().matches("^(true|false)$")) {
                throw new IllegalArgumentException("Wartość powiadomień e-mail musi być 'true' lub 'false'");
            }
            boolean oldValue = user.getNotificationsByEmail();
            boolean newValue = Boolean.parseBoolean(request.notificationsByEmail());
            user.setNotificationsByEmail(newValue);

            if (!oldValue && newValue) {
                emailService.sendUserOptInWelcomeEmail(user.getEmail(), user.getFirstName());
            }
        }

        if (request.notificationsByPhone() != null) {
            if (!request.notificationsByPhone().matches("^(true|false)$")) {
                throw new IllegalArgumentException("Wartość powiadomień telefonicznych musi być 'true' lub 'false'");
            }
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

    private void validateAndUpdateName(String flag, String name, User user) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException(("First".equals(flag) ? "Imię" : "Nazwisko") + " nie może być puste");
        }
        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException(("First".equals(flag) ? "Imię" : "Nazwisko") + " musi zawierać od 2 do 50 znaków");
        }
        if (!name.matches("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]+$")) {
            throw new IllegalArgumentException(("First".equals(flag) ? "Imię" : "Nazwisko") + " może zawierać tylko litery, spacje i myślniki");
        }
        if ("First".equals(flag)) {
            user.setFirstName(name);
        } else {
            user.setLastName(name);
        }
    }

    private void validateAndUpdateEmail(UserDetailsRequest request, User user) {
        if (!request.email().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new IllegalArgumentException("Nieprawidłowy format adresu e-mail");
        }
        if (!request.email().equals(user.getEmail()) &&
                userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Adres e-mail jest już używany");
        }
        user.setEmail(request.email());
    }

    @Transactional
    public void updateUserPassword(UserDetails userDetails, UpdatePasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Żądanie aktualizacji hasła nie może być puste");
        }

        if (request.newPassword() == null || request.oldPassword() == null || request.confirmNewPassword() == null) {
            throw new IllegalArgumentException("Hasła nie mogą być puste");
        }

        if (request.newPassword().length() < 6) {
            throw new IllegalArgumentException("Nowe hasło musi mieć co najmniej 6 znaków");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie został znaleziony"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Aktualne hasło jest nieprawidłowe");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("Nowe hasło i potwierdzenie nie są zgodne");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Nowe hasło musi być różne od obecnego hasła");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Hasło zostało pomyślnie zaktualizowane dla użytkownika o ID: {}", user.getUserId());
        emailService.sendUserPasswordChangedEmail(user.getEmail(), user.getFirstName());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Nie znaleziono użytkownika o emailu: " + email)
                );
    }

    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }
}
