package io.cylonsam.cajucreditcardauthorizer.infra.repository;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Optional<Merchant> findByName(String name);
}
