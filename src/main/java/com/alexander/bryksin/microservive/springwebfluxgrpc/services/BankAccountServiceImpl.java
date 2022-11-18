package com.alexander.bryksin.microservive.springwebfluxgrpc.services;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.repositories.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public Mono<BankAccount> createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount)
                .doOnEach(signal -> log.info("signal: {}", signal.get()))
                .doOnSuccess(savedBankAccount -> log.info("saved bank account: {}", savedBankAccount));
    }

    @Override
    public Mono<BankAccount> getBankAccountById(UUID id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public Mono<BankAccount> depositAmount(UUID id, BigDecimal amount) {
        return bankAccountRepository.findById(id).flatMap(bankAccount -> {
            bankAccount.setBalance(bankAccount.getBalance().add(amount));
            return bankAccountRepository.save(bankAccount);
        }).doOnNext(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    public Mono<BankAccount> withdrawAmount(UUID id, BigDecimal amount) {
        return bankAccountRepository.findById(id).flatMap(bankAccount -> {
            bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
            return bankAccountRepository.save(bankAccount);
        }).doOnNext(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    public Flux<BankAccount> findBankAccountByBalanceBetween(BigDecimal min, BigDecimal max, Pageable pageable) {
        return bankAccountRepository.findBankAccountByBalanceBetween(min, max, pageable);
    }
}
