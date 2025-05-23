package com.agh.zlotowka.handler;

import com.agh.zlotowka.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal Argument Error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundExceptions(EntityNotFoundException e) {
        log.error("Entity Not Found Error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        log.error("Internal server error", e);
        return ResponseEntity
                .internalServerError()
                .body(Map.of("error", "Wystąpił nieoczekiwany błąd. Proszę spróbować ponownie później"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Nie znaleziono żądanego adresu URL\n"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseException(HttpMessageNotReadableException e) {
        log.error("Received invalid request body: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", "Nieprawidłowa treść żądania\n"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Żądany endpoint nie istnieje"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Nieprawidłowy typ parametru '%s'. Oczekiwano %s, otrzymano %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName(),
                ex.getValue());
        return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Metoda " + ex.getMethod() + " nie jest obsługiwana dla tego endpointu."));
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Niepoprawny adres e-mail lub hasło"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(CurrencyConversionException.class)
    public ResponseEntity<Map<String, String>> handleCurrencyConversionException(CurrencyConversionException e) {
        log.error("Currency conversion error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", e.getMessage()));

    }

    @ExceptionHandler(BudgetInsufficientException.class)
    public ResponseEntity<Map<String, String>> handleBudgetInsufficientException(BudgetInsufficientException e) {
        log.error("Budget insufficient error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(InsufficientBudgetException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBudgetException(InsufficientBudgetException e) {
        log.error("Insufficient budget error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(PlanAmountExceededException.class)
    public ResponseEntity<Map<String, String>> handlePlanAmountExceededException(PlanAmountExceededException e) {
        log.error("Plan amount exceeded error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(PlanOwnershipException.class)
    public ResponseEntity<Map<String, String>> handlePlanOwnershipException(PlanOwnershipException e) {
        log.error("Plan ownership error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(PlanCompletionException.class)
    public ResponseEntity<Map<String, String>> handlePlanCompletionException(PlanCompletionException e) {
        log.error("Plan completion error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("error", e.getMessage()));
    }
}
