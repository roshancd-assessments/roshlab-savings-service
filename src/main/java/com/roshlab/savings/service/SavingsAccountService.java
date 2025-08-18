package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.exception.MaxAccountsPerCustomerException;
import com.roshlab.savings.repository.SavingsAccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final NickNameValidatorService nickNameValidatorService;
    private final TaskExecutor preLoadDataToCacheTaskExecutor;

    // Create a new Savings Account
    @Caching(evict = {
            @CacheEvict(value = "accountsByCustomerName", key = "#savingsAccount.customerName"),
            @CacheEvict(value = "accountsById", key = "#result.id")
    })
    @CircuitBreaker(name = "createAccountCircuitBreaker", fallbackMethod = "createAccountFallback")
    public SavingsAccount createAccount(SavingsAccount savingsAccount) {
        log.info("Creating Savings Account for customer: {}", savingsAccount.getCustomerName());

        // Validate nickname before proceeding
        nickNameValidatorService.validate(savingsAccount.getAccountNickName());

        // Enforce business rule: limit each customer to a maximum of 5 savings accounts
        long count = savingsAccountRepository.countByCustomerName(savingsAccount.getCustomerName());
        if (count >= 5) {
            throw new MaxAccountsPerCustomerException("Customer cannot have more than 5 accounts");
        }

        // Store Savings Account
        savingsAccount.setAccountNumber(generateAccountNumber());
        return savingsAccountRepository.save(savingsAccount);
    }

    // Fallback method for createAccount
    public SavingsAccount createAccountFallback(SavingsAccount savingsAccount, Throwable throwable) {
        log.warn("Circuit breaker is open or a fallback was triggered for account creation. Reason: {}",
                throwable.getMessage());
        return null;
    }

    // Get a Savings Account by ID
    @TimeLimiter(name = "getAccountTimeLimiter")
    @Cacheable(value = "accountsById", key = "#id")
    @Retry(name = "getAccountRetry")
    @CircuitBreaker(name = "getAccountCircuitBreaker", fallbackMethod = "getAccountFallback")
    public CompletableFuture<SavingsAccount> getAccount(Long id) {
        log.info("Retrieving Savings Account with ID: {}", id);
        // Retrieve Savings Account by ID
        return CompletableFuture.supplyAsync(() -> savingsAccountRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Account not found")), preLoadDataToCacheTaskExecutor);
    }

    // Fallback method for getAccount
    public CompletableFuture<SavingsAccount> getAccountFallback(Long id, Throwable throwable) {
        log.warn("Circuit breaker is open or a fallback was triggered for account ID: {}. Reason: {}",
                id, throwable.getMessage());
        return CompletableFuture.completedFuture(
                SavingsAccount.builderWithId()
                        .id(null)
                        .accountNumber("000000000000")
                        .customerName("fallback")
                        .accountNickName("fallback")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );
    }

    // Get all Savings Accounts for a specific customer
    @Cacheable(value = "accountsByCustomerName", key = "#customerName")
    @CircuitBreaker(name = "accountsByCustomerNameCircuitBreaker", fallbackMethod = "getAccountsByCustomerNameFallback")
    public List<SavingsAccount> getAccountsByCustomerName(String customerName) {
        log.info("Retrieving Savings Accounts for customer: {}", customerName);
        // Retrieve all Savings Accounts for a specific customer
        return savingsAccountRepository.findByCustomerName(customerName);
    }

    // Fallback method for getAccountsByCustomerName
    public List<SavingsAccount> getAccountsByCustomerNameFallback(String customerName, Throwable throwable) {
        log.warn("Circuit breaker is open or a fallback was triggered for customer: {}. Reason: {}",
                customerName, throwable.getMessage());
        return Collections.emptyList();
    }

    // Generate a unique account number
    private String generateAccountNumber() {
        // Simple 12-digit numeric example
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L));
    }
}
