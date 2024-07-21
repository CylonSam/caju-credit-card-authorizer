package io.cylonsam.cajucreditcardauthorizer.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMerchantMccRequestDTO(
        @NotBlank
        String mcc
) {
}
