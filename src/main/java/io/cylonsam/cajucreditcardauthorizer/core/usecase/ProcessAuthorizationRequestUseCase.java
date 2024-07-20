package io.cylonsam.cajucreditcardauthorizer.core.usecase;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import io.cylonsam.cajucreditcardauthorizer.core.domain.TransactionClassification;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessAuthorizationRequestUseCase {
    private final AccountRepository accountRepository;

    public ProcessAuthorizationRequestUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void process(AuthorizationTransaction authorizationTransaction) {
        System.out.println("Processing authorization request: " + authorizationTransaction);

        final Optional<Account> transactionAccount = accountRepository.findById(authorizationTransaction.getAccountId());
        if (transactionAccount.isEmpty()) {
            throw new IllegalArgumentException("Account %s not found".formatted(authorizationTransaction.getAccountId()));
        }

        final Account updatedAccount = debitAccount(transactionAccount.get(), authorizationTransaction);

        accountRepository.save(updatedAccount);
    }

    private TransactionClassification getTransactionClassification(String code) {
        return TransactionClassification.getClassification(code);
    }

    private Account debitAccount(final Account account, final AuthorizationTransaction authorizationTransaction) {
        final TransactionClassification transactionClassification = getTransactionClassification(authorizationTransaction.getMcc());

        switch (transactionClassification) {
            case FOOD:
                account.setFoodBalance(account.getFoodBalance() - authorizationTransaction.getAmount());
                break;
            case MEAL:
                account.setMealBalance(account.getMealBalance() - authorizationTransaction.getAmount());
                break;
            case CASH:
                account.setCashBalance(account.getCashBalance() - authorizationTransaction.getAmount());
                break;
        }

        return account;
    }

    private void executeTransaction(TransactionClassification transactionClassification) {


        switch (transactionClassification) {
            case FOOD:
                System.out.println("Food Transaction");
                break;
            case MEAL:
                System.out.println("Meal Transaction");
                break;
            case CASH:
                System.out.println("Cash Transaction");
                break;
        }
    }
}
