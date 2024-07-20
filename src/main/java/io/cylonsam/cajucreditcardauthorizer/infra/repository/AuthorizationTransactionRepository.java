package io.cylonsam.cajucreditcardauthorizer.infra.repository;

import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationTransactionRepository extends CrudRepository<AuthorizationTransaction, String> {}
