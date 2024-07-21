package io.cylonsam.cajucreditcardauthorizer.core.service;

import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import io.cylonsam.cajucreditcardauthorizer.core.domain.LockAccountService;
import io.cylonsam.cajucreditcardauthorizer.core.exception.UnprocessableRequestException;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AccountRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AuthorizationTransactionRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class AuthorizationRequestServiceTest {

    private final AuthorizationRequestService authorizationRequestService;
    private final AccountRepository accountRepository;
    private final AuthorizationTransactionRepository authorizationTransactionRepository;
    private final MerchantRepository merchantRepository;
    private final LockAccountService lockAccountService;

    AuthorizationRequestServiceTest() {
        this.accountRepository = mock(AccountRepository.class);
        this.authorizationTransactionRepository = mock(AuthorizationTransactionRepository.class);
        this.merchantRepository = mock(MerchantRepository.class);
        this.lockAccountService = mock(LockAccountService.class);
        this.authorizationRequestService = new AuthorizationRequestService(accountRepository, authorizationTransactionRepository, merchantRepository, lockAccountService);
    }

    @Test
    void shouldThrowUnprocessableRequestExceptionWhenAccountIsNotFound() {
        // Given account is not found
        final AuthorizationTransaction transaction = AuthorizationTransaction.builder()
                .account(Account.builder().id("1").build())
                .amount(100.0)
                .mcc("5411")
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        when(accountRepository.findById("1")).thenReturn(Optional.empty());
        // Should throw unprocessable request exception
        assertThrows(UnprocessableRequestException.class, () -> authorizationRequestService.process(transaction));
    }

    @Test
    void shouldLockAccountWhenProcessIsCalled() {
        // Given transaction 1 is processed
        final AuthorizationTransaction transaction1 = AuthorizationTransaction.builder()
                .account(Account.builder().id("1").build())
                .amount(100.0)
                .mcc("5411")
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        final AuthorizationTransaction transaction2 = AuthorizationTransaction.builder()
                .account(Account.builder().id("1").build())
                .amount(120.0)
                .mcc("5411")
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        final Account account1 = Account.builder()
                .id("1")
                .cashBalance(200.0)
                .foodBalance(200.0)
                .mealBalance(200.0)
                .build();

        when(accountRepository.findById("1")).thenReturn(Optional.of(account1));
        doNothing().when(lockAccountService).lockAccount("1");

        authorizationRequestService.process(transaction1);
        verify(lockAccountService, times(1)).getAccountLockStatus("1");
        verify(lockAccountService, times(1)).lockAccount("1");

        when(lockAccountService.getAccountLockStatus("1")).thenReturn(Optional.of(true));
        assertThrows(UnprocessableRequestException.class, () -> authorizationRequestService.process(transaction2));
    }
}
