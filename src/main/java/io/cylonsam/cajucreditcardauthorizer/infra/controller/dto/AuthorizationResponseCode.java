package io.cylonsam.cajucreditcardauthorizer.infra.controller.dto;

import lombok.Getter;

@Getter
public enum AuthorizationResponseCode {
    AUTHORIZED("00"),
    INSUFFICIENT_BALANCE("51"),
    UNPROCESSABLE_REQUEST("07");

    private final String code;

    AuthorizationResponseCode(final String code) {
        this.code = code;
    }
}
