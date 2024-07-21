package io.cylonsam.cajucreditcardauthorizer.infra.controller;


import io.cylonsam.cajucreditcardauthorizer.BaseIntegrationTest;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Account;
import io.cylonsam.cajucreditcardauthorizer.core.domain.AuthorizationTransaction;
import io.cylonsam.cajucreditcardauthorizer.core.domain.Merchant;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AccountRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.AuthorizationTransactionRepository;
import io.cylonsam.cajucreditcardauthorizer.infra.repository.MerchantRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorizationTransactionServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthorizationTransactionRepository authorizationTransactionRepository;
    @Autowired
    private MerchantRepository merchantRepository;

    @AfterEach
    void clearDatabase() {
        authorizationTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldAuthorizeMealTransactionGivenValidRequest() {
        // Given a valid request
        final double mealBalance = 150.0;
        final String mealMcc = "5811";
        final String accountId = createAccount(200.0, mealBalance, 180.0);

        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(accountId)
                .totalAmount(100.0)
                .mcc(mealMcc)
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":\"00\"}"));

        // Then the transaction is authorized
        final Optional<Account> account = accountRepository.findById(authorizationRequestDTO.getAccount());
        assertTrue(account.isPresent());
        assertEquals(mealBalance - authorizationRequestDTO.getTotalAmount(), account.get().getMealBalance());

        final List<AuthorizationTransaction> transactions = authorizationTransactionRepository.findAllByAccountId(accountId);
        assertFalse(transactions.isEmpty());
        assertEquals(authorizationRequestDTO.getTotalAmount(), transactions.getFirst().getAmount());
        assertEquals(mealMcc, transactions.getFirst().getMcc());
    }

    @Test
    @SneakyThrows
    void shouldNotAuthorizeMealTransactionGivenInsufficientMealBalanceAndCashBalance() {
        // Given a valid request
        final double mealBalance = 80.0;
        final String mealMcc = "5811";
        final String accountId = createAccount(200.0, mealBalance, 50.0);

        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(accountId)
                .totalAmount(100.0)
                .mcc(mealMcc)
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                // Then the transaction is not authorized and returns code 51
                .andExpect(content().json("{\"code\":\"51\"}"));

        final Optional<Account> account = accountRepository.findById(authorizationRequestDTO.getAccount());
        assertTrue(account.isPresent());
        assertEquals(mealBalance, account.get().getMealBalance());

        final List<AuthorizationTransaction> transactions = authorizationTransactionRepository.findAllByAccountId(accountId);
        assertTrue(transactions.isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldAuthorizeCashTransactionGivenSufficientCashBalanceAndUnknownMcc() {
        // Given a valid request
        final double cashBalance = 120.0;
        final String unknownMcc = "1234";
        final String accountId = createAccount(200.0, 80.0, cashBalance);

        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(accountId)
                .totalAmount(100.0)
                .mcc(unknownMcc)
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                // Then the transaction is authorized
                .andExpect(content().json("{\"code\":\"00\"}"));

        final Optional<Account> account = accountRepository.findById(authorizationRequestDTO.getAccount());
        assertTrue(account.isPresent());
        assertEquals(cashBalance - authorizationRequestDTO.getTotalAmount(), account.get().getCashBalance());

        final List<AuthorizationTransaction> transactions = authorizationTransactionRepository.findAllByAccountId(accountId);
        assertFalse(transactions.isEmpty());
        assertEquals(authorizationRequestDTO.getTotalAmount(), transactions.getFirst().getAmount());
    }

    @Test
    @SneakyThrows
    void shouldAuthorizeFoodTransactionByDebitingCashBalanceGivenInsufficientFoodBalanceAndSufficientCashBalance() {
        // Given a valid request
        final double foodBalance = 30.0;
        final double cashBalance = 120.0;
        final String foodMcc = "5411";
        final String accountId = createAccount(foodBalance, 80.0, cashBalance);

        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(accountId)
                .totalAmount(100.0)
                .mcc(foodMcc)
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                // Then the transaction is authorized
                .andExpect(content().json("{\"code\":\"00\"}"));

        final Optional<Account> account = accountRepository.findById(authorizationRequestDTO.getAccount());
        assertTrue(account.isPresent());
        assertEquals(cashBalance - authorizationRequestDTO.getTotalAmount(), account.get().getCashBalance());
        assertEquals(foodBalance, account.get().getFoodBalance());

        final List<AuthorizationTransaction> transactions = authorizationTransactionRepository.findAllByAccountId(accountId);
        assertFalse(transactions.isEmpty());
        assertEquals(authorizationRequestDTO.getTotalAmount(), transactions.getFirst().getAmount());
        assertEquals(foodMcc, transactions.getFirst().getMcc());
    }

    @Test
    @SneakyThrows
    void shouldNotAuthorizeTransactionGivenInvalidRequest() {
        // Given an invalid request
        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(null)
                .totalAmount(100.0)
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                // Then the transaction is not authorized
                .andExpect(content().json("{\"code\":\"07\"}"));
    }

    @Test
    @SneakyThrows
    void shouldAuthorizeFoodTransactionGivenIncorrectMccWhenMerchantMccIsAssociatedWithFoodTransaction() {
        // Given a valid request
        final double foodBalance = 200.0;
        final String incorrectMcc = "1234";
        final String accountId = createAccount(foodBalance, 80.0, 150.0);
        final String merchantName = "PADARIA DO ZE               SAO PAULO BR";
        final String merchantMcc = "5411";
        final String merchantId = createMerchant(merchantMcc, merchantName);

        final AuthorizationRequestDTO authorizationRequestDTO = AuthorizationRequestDTO.builder()
                .account(accountId)
                .totalAmount(100.0)
                .mcc(incorrectMcc)
                .merchant(merchantName)
                .build();

        // When the request is processed
        mvc.perform(post("/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationRequestDTO)))
                .andExpect(status().isOk())
                // Then the transaction is authorized
                .andExpect(content().json("{\"code\":\"00\"}"));

        // Then the transaction is authorized
        final Optional<Account> account = accountRepository.findById(authorizationRequestDTO.getAccount());
        assertTrue(account.isPresent());
        assertEquals(foodBalance - authorizationRequestDTO.getTotalAmount(), account.get().getFoodBalance());

        final List<AuthorizationTransaction> transactions = authorizationTransactionRepository.findAllByAccountId(accountId);
        assertFalse(transactions.isEmpty());
        assertEquals(authorizationRequestDTO.getTotalAmount(), transactions.getFirst().getAmount());
        assertEquals(merchantMcc, transactions.getFirst().getMcc());

        final Optional<Merchant> merchant = merchantRepository.findById(merchantId);
        assertTrue(merchant.isPresent());
        assertEquals(merchantName, merchant.get().getName());
        assertEquals(merchantMcc, merchant.get().getMcc());
    }

    private String createAccount(final double foodBalance, final double mealBalance, final double cashBalance) {
        final Account account = Account.builder()
                .foodBalance(foodBalance)
                .mealBalance(mealBalance)
                .cashBalance(cashBalance)
                .build();
        return accountRepository.save(account).getId();
    }

    private String createMerchant(final String mcc, final String name) {
        final Merchant merchant = Merchant.builder()
                .mcc(mcc)
                .name(name)
                .build();
        return merchantRepository.save(merchant).getId();
    }
}
