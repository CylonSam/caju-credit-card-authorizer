package io.cylonsam.cajucreditcardauthorizer.core.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(final String account) {
        super("Insufficient balance for account %s".formatted(account));
    }
}
