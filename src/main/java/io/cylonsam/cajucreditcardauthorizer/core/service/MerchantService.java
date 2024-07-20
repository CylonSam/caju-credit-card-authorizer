package io.cylonsam.cajucreditcardauthorizer.core.service;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(final MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public void addMerchantMcc(final Merchant merchant) {
        merchantRepository.save(merchant);
    }
}
