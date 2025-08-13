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
        SavingsAccount account = new SavingsAccount();
        account.setCustomerName("Roshan Deniyage");
        account.setAccountNumber("123456789123");
        account.setAccountNickName("EmergencyFund");

        repository.save(account);

        List<SavingsAccount> accounts = repository.findByCustomerName("Roshan Deniyage");
        assertEquals(1, accounts.size());
        assertEquals("123456789123", accounts.get(0).getAccountNumber());
    }
}
