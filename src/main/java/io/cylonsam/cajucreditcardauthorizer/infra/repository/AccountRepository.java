package io.cylonsam.cajucreditcardauthorizer.infra.repository;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
}
