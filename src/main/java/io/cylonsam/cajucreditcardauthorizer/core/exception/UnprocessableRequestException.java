package io.cylonsam.cajucreditcardauthorizer.core.exception;

public class UnprocessableRequestException extends RuntimeException{
    public UnprocessableRequestException(final String message) {
        super(message);
    }
}
