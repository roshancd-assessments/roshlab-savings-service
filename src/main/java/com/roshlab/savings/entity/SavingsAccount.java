package com.roshlab.savings.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "savings_account")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Setter
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Setter
    @Column(name = "account_nick_name", length = 30)
    private String accountNickName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Use a no-args constructor for JPA only
    public SavingsAccount() {
    }

    // Builder constructor for creating instances with an ID. This is for testing purposes.
    @Builder(builderMethodName = "builderWithId")
    private SavingsAccount(Long id, String accountNumber, String customerName, String accountNickName, OffsetDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.accountNickName = accountNickName;
        this.createdAt = createdAt;
    }

}