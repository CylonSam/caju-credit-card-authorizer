package io.cylonsam.cajucreditcardauthorizer.core.service;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification;
import io.cylonsam.cajucreditcardauthorizer.core.exception.InsufficientBalanceException;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AccountRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AuthorizationTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification.CASH;
import static io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification.getClassification;

@Service
public class ProcessAuthorizationRequestService {
    private final AccountRepository accountRepository;
    private final AuthorizationTransactionRepository transactionRepository;

    public ProcessAuthorizationRequestService(final AccountRepository accountRepository,
                                              final AuthorizationTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void process(final AuthorizationTransaction authorizationTransaction) {
        System.out.println("Processing authorization request: " + authorizationTransaction);

        final Optional<Account> transactionAccount = accountRepository.findById(authorizationTransaction.getAccountId());
        if (transactionAccount.isEmpty()) {
            throw new IllegalArgumentException("Account %s not found".formatted(authorizationTransaction.getAccountId()));
        }

        final Account updatedAccount = debitAccount(transactionAccount.get(), authorizationTransaction);

        accountRepository.save(updatedAccount);
        transactionRepository.save(createTransaction(updatedAccount, authorizationTransaction));
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
}
