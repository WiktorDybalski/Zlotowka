package com.agh.zlotowka.exception;

public class BudgetInsufficientException extends RuntimeException {
    public BudgetInsufficientException(String message) {
        super(message);
    }
}
