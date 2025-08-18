package com.roshlab.savings.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "savings_account")
@NoArgsConstructor
public class SavingsAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "account_nick_name", length = 30)
    private String accountNickName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

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