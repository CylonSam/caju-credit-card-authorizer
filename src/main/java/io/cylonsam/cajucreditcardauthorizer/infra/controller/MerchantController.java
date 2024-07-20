package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.service.MerchantService;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.MerchantRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(final MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/merchant")
    ResponseEntity<?> addMerchantMcc(@RequestBody final MerchantRequestDTO merchantRequestDTO) {
        RequestValidator.validate(merchantRequestDTO);
        merchantService.addMerchantMcc(merchantRequestDTO.toMerchant());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
