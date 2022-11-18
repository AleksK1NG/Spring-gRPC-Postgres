package com.alexander.bryksin.microservive.springwebfluxgrpc.mappers;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.grpc.bankService.BankAccountData;
import com.grpc.bankService.CreateBankAccountRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class BankAccountMapper {
    private BankAccountMapper() {
    }

    public static BankAccount of(CreateBankAccountRequest req) {
        return BankAccount.builder()
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .currency(req.getCurrency())
                .balance(BigDecimal.valueOf(req.getBalance()))
                .phone(req.getPhone())
                .address(req.getAddress())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BankAccountData toGrpc(BankAccount bankAccount) {
        return BankAccountData.newBuilder()
                .setId(bankAccount.getId().toString())
                .setEmail(bankAccount.getEmail())
                .setFirstName(bankAccount.getFirstName())
                .setLastName(bankAccount.getLastName())
                .setBalance(bankAccount.getBalance().doubleValue())
                .setCurrency(bankAccount.getCurrency())
                .setAddress(bankAccount.getAddress())
                .setPhone(bankAccount.getPhone())
                .setCreatedAt(bankAccount.getCreatedAt().toString())
                .setUpdatedAt(bankAccount.getUpdatedAt().toString())
                .build();
    }
}