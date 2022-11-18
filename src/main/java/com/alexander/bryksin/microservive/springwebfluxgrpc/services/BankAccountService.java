package com.alexander.bryksin.microservive.springwebfluxgrpc.services;


import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountService {
    Mono<BankAccount> createBankAccount(BankAccount bankAccount);

    Mono<BankAccount> getBankAccountById(UUID id);

    Mono<BankAccount> depositAmount(UUID id, BigDecimal amount);

    Mono<BankAccount> withdrawAmount(UUID id, BigDecimal amount);
}
