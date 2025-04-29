package com.agh.zlotowka.exception;

public class PlanAmountExceededException extends RuntimeException {
    public PlanAmountExceededException(String message) {
        super(message);
    }
}
