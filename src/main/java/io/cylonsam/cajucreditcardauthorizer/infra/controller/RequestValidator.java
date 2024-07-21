package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.exception.UnprocessableRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class RequestValidator {
    public static void validate(final Object request) throws UnprocessableRequestException {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        final Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new UnprocessableRequestException("Invalid request");
        }
    }
}
