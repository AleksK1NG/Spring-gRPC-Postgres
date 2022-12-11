package com.alexander.bryksin.microservive.springwebfluxgrpc.services;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.FindByBalanceRequestDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.BankAccountNotFoundException;
import com.alexander.bryksin.microservive.springwebfluxgrpc.repositories.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<BankAccount> createBankAccount(@SpanTag(key = "bankAccount") BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount)
                .doOnSuccess(savedBankAccount -> spanTag("savedBankAccount", savedBankAccount.toString()))
                .doOnError(this::spanError);
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Mono<BankAccount> getBankAccountById(@SpanTag(key = "id") UUID id) {
        return bankAccountRepository.findById(id)
                .doOnEach(v -> spanTag("id", id.toString()))
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .doOnError(this::spanError);
    }

    @Override
    @Transactional
    @NewSpan
    public Mono<BankAccount> depositAmount(@SpanTag(key = "id") UUID id, @SpanTag(key = "amount") BigDecimal amount) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .flatMap(bankAccount -> bankAccountRepository.save(bankAccount.depositBalance(amount)))
                .doOnError(this::spanError)
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .doOnSuccess(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    @Transactional
    @NewSpan
    public Mono<BankAccount> withdrawAmount(@SpanTag(key = "id") UUID id, @SpanTag(key = "amount") BigDecimal amount) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankAccountNotFoundException(id.toString())))
                .flatMap(bankAccount -> bankAccountRepository.save(bankAccount.withdrawBalance(amount)))
                .doOnError(this::spanError)
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .doOnSuccess(bankAccount -> log.info("updated bank account: {}", bankAccount));
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Flux<BankAccount> findBankAccountByBalanceBetween(@SpanTag(key = "request") FindByBalanceRequestDto request) {
        return bankAccountRepository.findBankAccountByBalanceBetween(request.min(), request.max(), request.pageable())
                .doOnError(this::spanError);
    }

    @Override
    @Transactional(readOnly = true)
    @NewSpan
    public Mono<Page<BankAccount>> findAllBankAccountsByBalance(@SpanTag(key = "request") FindByBalanceRequestDto request) {
        return bankAccountRepository.findAllBankAccountsByBalance(request.min(), request.max(), request.pageable())
                .doOnError(this::spanError)
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    private void spanTag(String key, String value) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.tag(key, value));
    }

    private void spanError(Throwable ex) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.error(ex));
    }
}
