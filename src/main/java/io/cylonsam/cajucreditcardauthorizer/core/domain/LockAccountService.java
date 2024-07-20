package io.cylonsam.cajucreditcardauthorizer.core.domain;

import java.util.Optional;

public interface LockAccountService {
    void lockAccount(final String accountId);
    void unlockAccount(final String accountId);
    Optional<Boolean> getAccountLockStatus(final String accountId);
}
