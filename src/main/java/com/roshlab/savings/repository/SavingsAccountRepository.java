package com.roshlab.savings.repository;

import com.roshlab.savings.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
    long countByCustomerName(String customerName);
}

