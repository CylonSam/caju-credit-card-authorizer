package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.cylonsam.cajucreditcardauthorizer.BaseIntegrationTest;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.MerchantRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.UpdateMerchantMccRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        assertEquals(merchantRequestDTO.getMcc(), merchant.get().getMcc());
    }

    @Test
    @SneakyThrows
    void shouldUpdateMerchantMcc() {
        // Given a valid request
        final UpdateMerchantMccRequestDTO updateMerchantMccRequestDTO = new UpdateMerchantMccRequestDTO("5811");
        final Merchant merchantId = merchantRepository.save(Merchant.builder()
                .name("PADARIA DO ZE               SAO PAULO BR")
                .mcc("5411")
                .build());
        assertTrue(merchantRepository.findById(merchantId.getId()).isPresent());

        // When the request is processed
        mvc.perform(put("/merchant/{id}", merchantId.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMerchantMccRequestDTO)))
                .andExpect(status().isOk());

        // Then the merchant is updated
        final Optional<Merchant> merchant = merchantRepository.findById(merchantId.getId());
        assertTrue(merchant.isPresent());
        assertEquals(updateMerchantMccRequestDTO.mcc(), merchant.get().getMcc());
    }

    @Test
    @SneakyThrows
    void shouldDeleteMerchantMcc() {
        // Given
        final Merchant merchantId = merchantRepository.save(Merchant.builder()
                .name("PADARIA DO ZE               SAO PAULO BR")
                .mcc("5411")
                .build());
        assertTrue(merchantRepository.findById(merchantId.getId()).isPresent());

        // When
        mvc.perform(delete("/merchant/{id}", merchantId.getId()))
                .andExpect(status().isOk());

        // Then
        assertFalse(merchantRepository.findById(merchantId.getId()).isPresent());
    }

    @Test
    @SneakyThrows
    void shouldGetMerchants() {
        // Given
        final Merchant merchant1 = Merchant.builder()
                .name("PADARIA DO ZE               SAO PAULO BR")
                .mcc("5411")
                .build();
        final Merchant merchant2 = Merchant.builder()
                .name("UBER TRIP                   SAO PAULO BR")
                .mcc("5411")
                .build();
        merchantRepository.saveAll(List.of(merchant1, merchant2));

        // When
        mvc.perform(get("/merchant")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        final List<Merchant> merchants = objectMapper.readValue(mvc.perform(get("/merchant")
                        .contentType(APPLICATION_JSON)).andReturn().getResponse().getContentAsString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Merchant.class));
        assertEquals(2, merchants.size());
        assertTrue(merchants.contains(merchant1));
        assertTrue(merchants.contains(merchant2));
    }
}
