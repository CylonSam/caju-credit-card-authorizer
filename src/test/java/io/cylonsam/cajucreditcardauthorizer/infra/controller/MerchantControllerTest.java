package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.BaseIntegrationTest;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.MerchantRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MerchantControllerTest extends BaseIntegrationTest {
    @Autowired
    private MerchantRepository merchantRepository;

    @AfterEach
    void clearDatabase() {
        merchantRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldAddMerchantMcc() {
        // Given a valid request
        final MerchantRequestDTO merchantRequestDTO = MerchantRequestDTO.builder()
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .mcc("5811")
                .build();

        // When the request is processed
        mvc.perform(post("/merchant")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantRequestDTO)))
                .andExpect(status().isCreated());

        // Then the merchant is added
        final Optional<Merchant> merchant = merchantRepository.findByName("PADARIA DO ZE               SAO PAULO BR");
        assertTrue(merchant.isPresent());
    }
}
