package com.roshlab.savings.repository;

import com.roshlab.savings.AbstractSavingsApplicationTests;
import com.roshlab.savings.entity.SavingsAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SavingsAccountRepositoryTest extends AbstractSavingsApplicationTests {

    @Autowired
    private SavingsAccountRepository repository;

    @Test
    void testSaveAndFind() {
        // Given
        SavingsAccount account = new SavingsAccount();
        account.setCustomerName("Roshan Deniyage");
        account.setAccountNumber("123456789123");
        account.setAccountNickName("EmergencyFund");

        // When
        repository.save(account);

        // Then
        List<SavingsAccount> accounts = repository.findByCustomerName("Roshan Deniyage");
        assertEquals(1, accounts.size());
        assertEquals("123456789123", accounts.get(0).getAccountNumber());
    }

    @Test
    void testSaveMultipleAccountsForSameCustomer() {
        // Given
        SavingsAccount firstAccount = new SavingsAccount();
        firstAccount.setCustomerName("Roshan Deniyage");
        firstAccount.setAccountNumber("111111111111");
        firstAccount.setAccountNickName("PrimarySavings");

        SavingsAccount secondAccount = new SavingsAccount();
        secondAccount.setCustomerName("Roshan Deniyage");
        secondAccount.setAccountNumber("222222222222");
        secondAccount.setAccountNickName("SchoolFund");

        // When
        repository.save(firstAccount);
        repository.save(secondAccount);

        // Then
        Long numberOfAccounts = repository.countByCustomerName("Roshan Deniyage");
        assertEquals(2, numberOfAccounts);
    }
}
