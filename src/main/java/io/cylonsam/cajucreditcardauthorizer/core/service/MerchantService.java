package io.cylonsam.cajucreditcardauthorizer.core.service;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import io.cylonsam.cajucreditcardauthorizer.core.exception.UnprocessableRequestException;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(final MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public void addMerchantMcc(final Merchant merchant) {
        if (merchantRepository.findByName(merchant.getName()).isPresent()) {
            log.warn("Merchant with name {} already exists", merchant.getName());
            throw new UnprocessableRequestException("Merchant with name %s already exists".formatted(merchant.getName()));
        }

        merchantRepository.save(merchant);
        log.info("Merchant with name {} added successfully", merchant.getName());
    }

    public void updateMerchantMcc(final String id, final String mcc) {
        final Optional<Merchant> currentMerchant = merchantRepository.findById(id);
        if (currentMerchant.isEmpty()) {
            log.warn("Merchant with id {} does not exist", id);
            throw new UnprocessableRequestException("Merchant with id %s does not exist".formatted(id));
        }

        currentMerchant.get().setMcc(mcc);
        merchantRepository.save(currentMerchant.get());
        log.info("Merchant with id {} updated successfully", id);
    }


    public void removeMerchantMcc(final String merchantId) {
        final Optional<Merchant> currentMerchant = merchantRepository.findById(merchantId);
        if (currentMerchant.isEmpty()) {
            throw new UnprocessableRequestException("Merchant with id %s does not exist".formatted(merchantId));
        }

        merchantRepository.delete(currentMerchant.get());
        log.info("Merchant with id {} removed successfully", merchantId);
    }

    public Merchant getMerchant(String name) {
        final Optional<Merchant> merchant = merchantRepository.findByName(name);
        if (merchant.isEmpty()) {
            log.warn("Merchant with name {} does not exist", name);
            throw new UnprocessableRequestException("Merchant with name %s does not exist".formatted(name));
        }

        return merchant.get();
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }
}
