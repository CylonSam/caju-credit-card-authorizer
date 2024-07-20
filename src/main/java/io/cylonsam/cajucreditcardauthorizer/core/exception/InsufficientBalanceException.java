package io.cylonsam.cajucreditcardauthorizer.core.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance for account");
    }
}
