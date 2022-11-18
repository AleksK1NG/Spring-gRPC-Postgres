package com.alexander.bryksin.microservive.springwebfluxgrpc.domain;


import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.InvalidAmountException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Table(schema = "microservices", name = "bank_accounts")
public class BankAccount {

    @Id
    @Column("bank_account_id")
    private UUID id;

    @Column("email")
    private String email;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("address")
    private String address;

    @Column("phone")
    private String phone;

    @Column("currency")
    private String currency;

    @Column("balance")
    private BigDecimal balance;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;


    public BankAccount depositBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidAmountException(String.format("invalid amount %s for bank account: %s", amount, id));
        balance = balance.add(amount);
        return this;
    }

    public BankAccount withdrawBalance(BigDecimal amount) {
        var currentBalance = balance.subtract(amount);
        if (amount.compareTo(BigDecimal.ZERO) < 0 || currentBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidAmountException(String.format("invalid amount %s for bank account: %s", amount, id));
        return this;
    }
}
