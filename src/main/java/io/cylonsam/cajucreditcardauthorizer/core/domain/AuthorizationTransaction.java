package io.cylonsam.cajucreditcardauthorizer.core.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class AuthorizationRequest {
    private String account;
    private Double totalAmount;
    private String mcc;
    private String merchant;
}
