package com.roshlab.savings.repository;

import com.roshlab.savings.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    long countByCustomerName(String customerName);
    List<SavingsAccount> findByCustomerName(String customerName);
}

