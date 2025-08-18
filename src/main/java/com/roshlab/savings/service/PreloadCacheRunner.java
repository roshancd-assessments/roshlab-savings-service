package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreloadCacheRunner implements CommandLineRunner {

    private final SavingsAccountRepository savingsAccountRepository;
    private final CacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        log.info("Pre-loading existing savings accounts into cache on startup.");
        Cache accountsByIdCache = cacheManager.getCache("accountsById");
        Cache accountsByCustomerNameCache = cacheManager.getCache("accountsByCustomerName");

        // Check if caches are available
        if (accountsByIdCache == null || accountsByCustomerNameCache == null) {
            log.error("Cache 'accountsById' or 'accountsByCustomerName' not found. Please check your CachingConfig.");
            return;
        }

        try {
            // Pre-load all existing accounts by ID
            savingsAccountRepository.findAll().forEach(account -> {
                accountsByIdCache.put(account.getId(), account);
                log.info("Pre-loaded account with ID: {}", account.getId());
            });

            // Pre-load all existing accounts by customer name
            savingsAccountRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.groupingBy(SavingsAccount::getCustomerName))
                    .forEach((customerName, accounts) -> {
                        accountsByCustomerNameCache.put(customerName, accounts);
                        log.info("Pre-loaded accounts for customer: {}", customerName);
                    });
        } catch (Exception e) {
            log.error("Failed to pre-load cache on startup. The application may be in an inconsistent state.", e);
            throw new RuntimeException("Cache pre-loading failed.", e);
        }
    }
}