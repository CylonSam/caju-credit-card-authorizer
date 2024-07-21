package io.cylonsam.cajucreditcardauthorizer.core.service;

import io.cylonsam.cajucreditcardauthorizer.core.domain.*;
import io.cylonsam.cajucreditcardauthorizer.core.exception.InsufficientBalanceException;
import io.cylonsam.cajucreditcardauthorizer.core.exception.UnprocessableRequestException;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AccountRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AuthorizationTransactionRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification.CASH;
import static io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification.getClassification;

@Slf4j
@Service
public class AuthorizationTransactionService {
    private final AccountRepository accountRepository;
    private final AuthorizationTransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final LockAccountService lockAccountService;

    public AuthorizationTransactionService(final AccountRepository accountRepository,
                                           final AuthorizationTransactionRepository transactionRepository,
                                           final MerchantRepository merchantRepository,
                                           final LockAccountService lockAccountService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.lockAccountService = lockAccountService;
    }

    public void process(final AuthorizationTransaction authorizationTransaction) {
        log.info("Processing transaction for account {}", authorizationTransaction.getAccountId());
        final String accountId = authorizationTransaction.getAccountId();

        final Optional<Account> transactionAccount = accountRepository.findById(accountId);
        if (transactionAccount.isEmpty()) {
            log.warn("Account {} not found", accountId);
            throw new UnprocessableRequestException("Account %s not found".formatted(accountId));
        }
        if (lockAccountService.getAccountLockStatus(accountId).isPresent()) {
            log.warn("Account {} is being used by another transaction", accountId);
            throw new UnprocessableRequestException("Account %s is being used by another transaction".formatted(accountId));
        }
        lockAccountService.lockAccount(authorizationTransaction.getAccountId());

        final String finalMcc = getMcc(authorizationTransaction);
        authorizationTransaction.setMcc(finalMcc);

        final Account updatedAccount = debitAccount(transactionAccount.get(), authorizationTransaction);

        accountRepository.save(updatedAccount);
        transactionRepository.save(createTransaction(updatedAccount, authorizationTransaction));

        lockAccountService.unlockAccount(authorizationTransaction.getAccountId());
        log.info("Transaction processed successfully for account {}", authorizationTransaction.getAccountId());
    }

    private Account debitAccount(final Account account, final AuthorizationTransaction authorizationTransaction) {
        final TransactionClassification transactionClassification = getClassification(authorizationTransaction.getMcc());
        final double amount = authorizationTransaction.getAmount();

        final double currentBalance = account.getBalanceByClassification(transactionClassification);

        if (currentBalance >= amount && transactionClassification != CASH) {
            account.setBalanceByClassification(transactionClassification, currentBalance - amount);
            return account;
        }

        final double cashBalance = account.getCashBalance();

        if (cashBalance >= amount) {
            account.setBalanceByClassification(CASH, cashBalance - amount);
            return account;
        }

        log.warn("Insufficient balance for account {}", account.getId());
        throw new InsufficientBalanceException();
    }

    private AuthorizationTransaction createTransaction(final Account account, final AuthorizationTransaction authorizationTransaction) {

        return AuthorizationTransaction.builder()
                .account(account)
                .amount(authorizationTransaction.getAmount())
                .mcc(authorizationTransaction.getMcc())
                .merchant(authorizationTransaction.getMerchant())
                .build();
    }

    private String getMcc(final AuthorizationTransaction authorizationTransaction) {
        final Optional<Merchant> merchant = merchantRepository.findByName(authorizationTransaction.getMerchant().toUpperCase());

        return merchant.isPresent() ? merchant.get().getMcc() : authorizationTransaction.getMcc();
    }



}
