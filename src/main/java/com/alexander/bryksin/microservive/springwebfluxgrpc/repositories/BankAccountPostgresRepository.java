package com.alexander.bryksin.microservive.springwebfluxgrpc.repositories;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BankAccountPostgresRepository {
    Mono<Page<BankAccount>> findAllBankAccountsByBalance(BigDecimal min, BigDecimal max, Pageable pageable);
}
