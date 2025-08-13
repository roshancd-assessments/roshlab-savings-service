package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.repository.SavingsAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
    }

    @Transactional
    public SavingsAccount createAccount(String customerName, String accountNickName) {
        // Business rule validation: maximum 5 accounts per customer
        long count = savingsAccountRepository.countByCustomerName(customerName);
        if (count >= 5) {
            throw new IllegalStateException("Customer cannot have more than 5 accounts");
        }

        // Create account
        SavingsAccount account = new SavingsAccount();
        account.setCustomerName(customerName);
        account.setAccountNickName(accountNickName);
        account.setAccountNumber(generateAccountNumber());
        return savingsAccountRepository.save(account);
    }

    public SavingsAccount getAccount(UUID id) {
        return savingsAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public List<SavingsAccount> getAccountsByCustomer(String customerName) {
        return savingsAccountRepository.findByCustomerName(customerName);
    }

    private String generateAccountNumber() {
        // Simple 12-digit numeric example
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L));
    }
}

