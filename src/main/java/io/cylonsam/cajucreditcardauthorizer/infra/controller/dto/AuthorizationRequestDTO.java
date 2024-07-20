package io.cylonsam.cajucreditcardauthorizer.infra.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationRequestDTO {
    @JsonProperty("account") @NotBlank
    private String account;
    @JsonProperty("totalAmount") @NotNull
    private Double totalAmount;
    @JsonProperty("mcc") @NotBlank @Positive
    private String mcc;
    @JsonProperty("merchant") @NotBlank
    private String merchant;

    public AuthorizationTransaction toAuthorizationRequest() {
        return AuthorizationTransaction.builder()
                .account(new Account(account))
                .amount(totalAmount)
                .mcc(mcc)
                .merchant(merchant)
                .build();
    }
}
