package com.roshlab.savings.repository;

import com.roshlab.savings.entity.SavingsAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
class SavingsAccountRepositoryTest {

    // Start a disposable Postgres container for tests with Testcontainers
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("savings")
            .withUsername("postgres")
            .withPassword("postgres");

    // Bind Spring Boot datasource properties to Testcontainers variables
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
