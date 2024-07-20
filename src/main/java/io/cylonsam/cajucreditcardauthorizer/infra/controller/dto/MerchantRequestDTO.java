package io.cylonsam.cajucreditcardauthorizer.infra.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantRequestDTO {
    @NotBlank
    private String merchant;
    @NotBlank @Length(min = 4, max = 4)
    private String mcc;

    public Merchant toMerchant() {
        return Merchant.builder()
                .name(merchant)
                .mcc(mcc)
                .build();
    }
}
