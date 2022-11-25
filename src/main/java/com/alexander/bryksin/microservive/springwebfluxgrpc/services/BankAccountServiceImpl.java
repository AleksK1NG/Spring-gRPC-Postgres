package com.alexander.bryksin.microservive.springwebfluxgrpc.services;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.BankAccountNotFoundException;
import com.alexander.bryksin.microservive.springwebfluxgrpc.repositories.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.instrument.web.WebFluxSleuthOperators;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final Tracer tracer;

    @Override
    @Transactional
    @NewSpan
    public Mono<BankAccount> createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount)
                .doOnEach(signal -> log.info("signal: {}", signal.get()))
                .doOnSuccess(savedBankAccount -> log.info("saved bank account: {}", savedBankAccount));
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Mono<BankAccount> getBankAccountById(UUID id) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .doOnEach(v -> {
                    Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.tag("ALEX", "PRO"));
                    log.info("CURRENT SPAN: {}", tracer.currentSpan().toString());
                })
                .doOnEach(WebFluxSleuthOperators
                        .withSpanInScope(SignalType.ON_NEXT, signal -> log.info("Hello from simple [{}]", signal.getContextView())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    @NewSpan
    public Mono<BankAccount> depositAmount(UUID id, BigDecimal amount) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .flatMap(bankAccount -> bankAccountRepository.save(bankAccount.depositBalance(amount))
                        .publishOn(Schedulers.boundedElastic()))
                .doOnNext(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    @Transactional
    @NewSpan
    public Mono<BankAccount> withdrawAmount(UUID id, BigDecimal amount) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .flatMap(bankAccount -> bankAccountRepository.save(bankAccount.withdrawBalance(amount))
                        .publishOn(Schedulers.boundedElastic()))
                .doOnNext(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Flux<BankAccount> findBankAccountByBalanceBetween(BigDecimal min, BigDecimal max, Pageable pageable) {
        return bankAccountRepository.findBankAccountByBalanceBetween(min, max, pageable).publishOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Mono<Page<BankAccount>> findAllBankAccountsByBalance(BigDecimal min, BigDecimal max, Pageable pageable) {
        return bankAccountRepository.findAllBankAccountsByBalance(min, max, pageable)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }
}
