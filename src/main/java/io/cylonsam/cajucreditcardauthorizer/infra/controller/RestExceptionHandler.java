package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.exception.InsufficientBalanceException;
import io.cylonsam.cajucreditcardauthorizer.core.exception.UnprocessableRequestException;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseCode.INSUFFICIENT_BALANCE;
import static io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseCode.UNPROCESSABLE_REQUEST;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InsufficientBalanceException.class)
    protected ResponseEntity<AuthorizationResponseDTO> handleInsufficientBalance(final InsufficientBalanceException exception) {
        return buildResponseEntity(INSUFFICIENT_BALANCE.getCode());
    }

    @ExceptionHandler({Exception.class, UnprocessableRequestException.class})
    protected ResponseEntity<AuthorizationResponseDTO> handleException(final Exception exception,
                                                                       final UnprocessableRequestException unprocessableRequestException) {
        return buildResponseEntity(UNPROCESSABLE_REQUEST.getCode());
    }

    private ResponseEntity<AuthorizationResponseDTO> buildResponseEntity(final String code) {
        return new ResponseEntity<>(new AuthorizationResponseDTO(code), HttpStatus.OK);
    }
}
