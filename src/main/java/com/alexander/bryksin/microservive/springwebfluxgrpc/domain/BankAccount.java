package com.alexander.bryksin.microservive.springwebfluxgrpc.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
@Table(schema = "microservices", name = "bank_accounts")
public class BankAccount {

    @Id
    @Column("bank_account_id")
    private String id;

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
    private BigDecimal createdAt;

    @Column("updated_at")
    private BigDecimal updatedAt;
}
