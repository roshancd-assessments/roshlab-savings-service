package com.roshlab.savings.service;

import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.exception.MaxAccountsPerCustomerException;
import com.roshlab.savings.exception.OffensiveNicknameException;
import com.roshlab.savings.repository.SavingsAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private NickNameValidatorService nickNameValidatorService;

    @Mock
    private TaskExecutor preLoadDataToCacheTaskExecutor;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Test
    void createAccount_shouldCreateAccount_whenCustomerHasLessThanFiveAccounts() {
        // Arrange Entity
        SavingsAccount newAccount = new SavingsAccount();
        newAccount.setCustomerName("Roshan Deniyage");
        newAccount.setAccountNickName("MainSavings");

        // Arrange Mock Behavior
        doNothing().when(nickNameValidatorService).validate(anyString());
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
        doNothing().when(nickNameValidatorService).validate(anyString());
        when(savingsAccountRepository.countByCustomerName("Roshan Deniyage")).thenReturn(5L);

        // Act & Assert
        Assertions.assertThrows(MaxAccountsPerCustomerException.class, () -> {
            savingsAccountService.createAccount(newAccount);
        });
    }

    @Test
    void createAccount_shouldThrowOffensiveNicknameException_whenNicknameIsOffensive() {
        // Arrange: Create an account with a offensive nickname
        SavingsAccount newAccount = new SavingsAccount();
        newAccount.setCustomerName("Roshan Deniyage");
        newAccount.setAccountNickName("fool");

        // Arrange Mock Behavior
        doThrow(new OffensiveNicknameException("Nickname contains offensive nickname."))
                .when(nickNameValidatorService)
                .validate(anyString());

        // Act & Assert
        Assertions.assertThrows(OffensiveNicknameException.class, () -> {
            savingsAccountService.createAccount(newAccount);
        });
    }

    @Test
    void getAccount_shouldReturnAccount_whenIdExists() {
        // Arrange :
        // This will configure the mocked TaskExecutor to execute the Runnable immediately on the same thread.
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(preLoadDataToCacheTaskExecutor).execute(any(Runnable.class));

        //  Workaround to get SavingsAccount with ID
        SavingsAccount existingAccount = SavingsAccount.builderWithId()
                .id(1L)
                .customerName("Roshan Deniyage")
                .accountNumber("123456789123")
                .build();

        // Arrange Mock Behavior
        when(savingsAccountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        // Act
        CompletableFuture<SavingsAccount> foundAccount = savingsAccountService.getAccount(1L);

        // Assert
        Assertions.assertEquals(1L, foundAccount.join().getId());
    }


    @Test
    void getAccount_shouldThrowException_whenIdDoesNotExist() {
        // Arrange :
        // This will configure the mocked TaskExecutor to execute the Runnable immediately on the same thread.
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(preLoadDataToCacheTaskExecutor).execute(any(Runnable.class));

        // Arrange Mock Behavior
        when(savingsAccountRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert : asynchronous task within a CompletableFuture actually throws CompletionException
        Assertions.assertThrows(CompletionException.class, () -> {
            savingsAccountService.getAccount(2L).join();
        });
    }
}