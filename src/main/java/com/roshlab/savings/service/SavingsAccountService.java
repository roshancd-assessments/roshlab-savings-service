package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.exception.MaxAccountsPerCustomerException;
import com.roshlab.savings.repository.SavingsAccountRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
    }

    public SavingsAccount createAccount(@NotNull SavingsAccount savingsAccount) {
        // Enforce business rule: limit each customer to a maximum of 5 savings accounts
        long count = savingsAccountRepository.countByCustomerName(savingsAccount.getCustomerName());
        if (count >= 5) {
            throw new MaxAccountsPerCustomerException("Customer cannot have more than 5 accounts");
        }
        // Store Savings Account
        savingsAccount.setAccountNumber(generateAccountNumber());
        return savingsAccountRepository.save(savingsAccount);
    }

    public SavingsAccount getAccount(Long id) {
        return savingsAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public List<SavingsAccount> getAccountsByCustomerName(String customerName) {
        return savingsAccountRepository.findByCustomerName(customerName);
    }

    private @NotNull String generateAccountNumber() {
        // Simple 12-digit numeric example
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L));
    }

}

