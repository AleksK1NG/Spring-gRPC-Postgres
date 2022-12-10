package com.alexander.bryksin.microservive.springwebfluxgrpc.domain;


import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.InvalidAmountException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Table(schema = "microservices", name = "bank_accounts")
public class BankAccount {

    @Id
    @Column(BANK_ACCOUNT_ID)
    private UUID id;

    @Column(EMAIL)
    @Email
    private String email;

    @Column(FIRST_NAME)
    @Size(min = 3, max = 60)
    private String firstName;

    @Column(LAST_NAME)
    @Size(min = 3, max = 60)
    private String lastName;

    @Column(ADDRESS)
    @Size(min = 3, max = 260)
    private String address;

    @Column(PHONE)
    @Size(min = 6, max = 10)
    private String phone;

    @Column(CURRENCY)
    private Currency currency;

    @Column(BALANCE)
    @DecimalMin(value = "0.0")
    private BigDecimal balance;

    @Column(CREATED_AT)
    private LocalDateTime createdAt;

    @Column(UPDATED_AT)
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
        balance = balance.subtract(amount);
        return this;
    }


    public static final String BANK_ACCOUNT_ID = "bank_account_id";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String CURRENCY = "currency";
    public static final String BALANCE = "balance";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
}
