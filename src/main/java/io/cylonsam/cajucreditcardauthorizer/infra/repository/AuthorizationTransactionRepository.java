package io.cylonsam.cajucreditcardauthorizer.infra.repository;

import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationTransactionRepository extends JpaRepository<AuthorizationTransaction, String> {
    @Query("select t from AuthorizationTransaction t where t.account.id = ?1")
    List<AuthorizationTransaction> findAllByAccountId(String accountId);
}
