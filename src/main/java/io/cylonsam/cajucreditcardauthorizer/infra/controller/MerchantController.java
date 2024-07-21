package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.service.MerchantService;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.MerchantRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.UpdateMerchantMccRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(final MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/merchant")
    ResponseEntity<?> getMerchants(@RequestParam(required = false) final String name) {
        if (name == null) {
            return new ResponseEntity<>(merchantService.getAllMerchants(), HttpStatus.OK);
        }

        return new ResponseEntity<>(merchantService.getMerchant(name), HttpStatus.OK);
    }

    @PostMapping("/merchant")
    ResponseEntity<?> addMerchantMcc(@RequestBody final MerchantRequestDTO merchantRequestDTO) {
        RequestValidator.validate(merchantRequestDTO);
        merchantService.addMerchantMcc(merchantRequestDTO.toMerchant());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/merchant/{id}")
    ResponseEntity<?> updateMerchantMcc(@RequestBody final UpdateMerchantMccRequestDTO updateMerchantMccRequestDTO,
                                        @PathVariable final String id) {
        RequestValidator.validate(updateMerchantMccRequestDTO);
        merchantService.updateMerchantMcc(id, updateMerchantMccRequestDTO.mcc());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/merchant/{id}")
    ResponseEntity<?> removeMerchantMcc(@PathVariable final String id) {
        merchantService.removeMerchantMcc(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
