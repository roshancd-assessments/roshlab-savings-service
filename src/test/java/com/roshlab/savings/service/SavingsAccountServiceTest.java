package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.exception.MaxAccountsPerCustomerException;
import com.roshlab.savings.repository.SavingsAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Test
    void createAccount_shouldCreateAccount_whenCustomerHasLessThanFiveAccounts() {
        // Arrange Entity
        SavingsAccount newAccount = new SavingsAccount();
        newAccount.setCustomerName("Roshan Deniyage");
        newAccount.setAccountNickName("MainSavings");

        // Arrange Mock Behavior
        when(savingsAccountRepository.countByCustomerName("Roshan Deniyage")).thenReturn(3L);
        // Workaround to get SavingsAccount with ID
        when(savingsAccountRepository.save(any(SavingsAccount.class)))
                .thenReturn(SavingsAccount.builderWithId()
                        .id(1L)
                        .accountNumber("123456789123")
                        .customerName("Roshan Deniyage")
                        .accountNickName("MainSavings")
                        .createdAt(OffsetDateTime.now())
                        .build());

        // Act
        SavingsAccount createdAccount = savingsAccountService.createAccount(newAccount);

        // Assert
        Assertions.assertNotNull(createdAccount.getAccountNumber());
        Assertions.assertEquals("Roshan Deniyage", createdAccount.getCustomerName());
        Assertions.assertEquals(1L, createdAccount.getId());
    }


    @Test
    void createAccount_shouldThrowException_whenCustomerHasFiveAccounts() {
        // Arrange: Workaround to get SavingsAccount with ID
        SavingsAccount newAccount = SavingsAccount.builderWithId()
                .id(1L)
                .accountNumber("123456789123")
                .customerName("Roshan Deniyage")
                .accountNickName("MainSavings")
                .createdAt(OffsetDateTime.now())
                .build();

        // Arrange Mock Behavior
        when(savingsAccountRepository.countByCustomerName("Roshan Deniyage")).thenReturn(5L);

        // Act & Assert
        Assertions.assertThrows(MaxAccountsPerCustomerException.class, () -> {
            savingsAccountService.createAccount(newAccount);
        });
    }


    @Test
    void getAccount_shouldReturnAccount_whenIdExists() {
        // Arrange : Workaround to get SavingsAccount with ID
        SavingsAccount existingAccount = SavingsAccount.builderWithId()
                .id(1L)
                .customerName("Roshan Deniyage")
                .accountNumber("123456789123")
                .build();

        // Arrange Mock Behavior
        when(savingsAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        // Act
        SavingsAccount foundAccount = savingsAccountService.getAccount(1L);

        // Assert
        Assertions.assertEquals(1L, foundAccount.getId());
    }


    @Test
    void getAccount_shouldThrowException_whenIdDoesNotExist() {
        // Arrange Mock Behavior
        when(savingsAccountRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            savingsAccountService.getAccount(2L);
        });
    }

}