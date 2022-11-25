package com.alexander.bryksin.microservive.springwebfluxgrpc.repositories;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountRepository extends ReactiveSortingRepository<BankAccount, UUID>, BankAccountPostgresRepository {
    Flux<BankAccount> findBankAccountByBalanceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
}
