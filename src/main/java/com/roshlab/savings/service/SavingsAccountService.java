package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.exception.MaxAccountsPerCustomerException;
import com.roshlab.savings.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final NickNameValidator nickNameValidator;

    // Create a new Savings Account
    public SavingsAccount createAccount(SavingsAccount savingsAccount) {
        log.info("Creating Savings Account for customer: {}", savingsAccount.getCustomerName());

        // Validate nickname before proceeding
        nickNameValidator.validate(savingsAccount.getAccountNickName());

        // Enforce business rule: limit each customer to a maximum of 5 savings accounts
        long count = savingsAccountRepository.countByCustomerName(savingsAccount.getCustomerName());
        if (count >= 5) {
            throw new MaxAccountsPerCustomerException("Customer cannot have more than 5 accounts");
        }

        // Store Savings Account
        savingsAccount.setAccountNumber(generateAccountNumber());
        return savingsAccountRepository.save(savingsAccount);
    }

    // Get a Savings Account by ID
    public SavingsAccount getAccount(Long id) {
        log.info("Retrieving Savings Account with ID: {}", id);
        // Retrieve Savings Account by ID
        return savingsAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // Get all Savings Accounts for a specific customer
    public List<SavingsAccount> getAccountsByCustomerName(String customerName) {
        log.info("Retrieving Savings Accounts for customer: {}", customerName);
        // Retrieve all Savings Accounts for a specific customer
        return savingsAccountRepository.findByCustomerName(customerName);
    }

    // Generate a unique account number
    private String generateAccountNumber() {
        // Simple 12-digit numeric example
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L));
    }
}