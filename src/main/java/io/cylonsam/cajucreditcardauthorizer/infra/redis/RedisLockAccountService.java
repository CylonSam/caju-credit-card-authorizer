package io.cylonsam.cajucreditcardauthorizer.infra.redis;

import io.cylonsam.cajucreditcardauthorizer.core.domain.LockAccountService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisLockAccountService implements LockAccountService {
    private final RedisService redisService;

    public RedisLockAccountService(final RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void lockAccount(String accountId) {
        redisService.putValue(accountId, true);
    }

    @Override
    public void unlockAccount(String accountId) {
        redisService.deleteValue(accountId);
    }

    @Override
    public Optional<Boolean> getAccountLockStatus(String accountId) {
        return redisService.getValue(accountId, Boolean.class);
    }
}
