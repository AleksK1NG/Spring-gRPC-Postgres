package com.alexander.bryksin.microservive.springwebfluxgrpc.repositories;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import java.util.UUID;

public interface BankAccountRepository extends ReactiveSortingRepository<BankAccount, UUID> {
}
