package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.grpc;

import com.alexander.bryksin.microservive.springwebfluxgrpc.mappers.BankAccountMapper;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import com.grpc.bankService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {

    private final BankAccountService bankAccountService;
    private final Tracer tracer;
    private static final Long timeoutMillis = 5000L;
    private final Validator validator;

    @Override
    @NewSpan
    public Mono<CreateBankAccountResponse> createBankAccount(Mono<CreateBankAccountRequest> request) {
        return request.flatMap(req -> bankAccountService.createBankAccount(validate(BankAccountMapper.of(req)))
                        .doOnEach(v -> spanTag("req", req.toString())))
                .publishOn(Schedulers.boundedElastic())
                .map(bankAccount -> CreateBankAccountResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    @Override
    @NewSpan
    public Mono<GetBankAccountByIdResponse> getBankAccountById(Mono<GetBankAccountByIdRequest> request) {
        return request.flatMap(req -> bankAccountService.getBankAccountById(UUID.fromString(req.getId()))
                        .doOnEach(v -> spanTag("id", req.getId()))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnSuccess(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                        .doOnError(ex -> log.error("getBankAccountById", ex))
                        .map(bankAccount -> GetBankAccountByIdResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }


    @Override
    @NewSpan
    public Mono<DepositBalanceResponse> depositBalance(Mono<DepositBalanceRequest> request) {
        return request
                .flatMap(req -> bankAccountService.depositAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance()))
                        .doOnEach(v -> spanTag("req", req.toString()))
                        .map(bankAccount -> DepositBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    @NewSpan
    public Mono<WithdrawBalanceResponse> withdrawBalance(Mono<WithdrawBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.withdrawAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance()))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnEach(v -> spanTag("req", req.toString()))
                        .map(bankAccount -> WithdrawBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    @NewSpan
    public Flux<GetAllByBalanceResponse> getAllByBalance(Mono<GetAllByBalanceRequest> request) {
        return request
                .flatMapMany(req -> bankAccountService.findBankAccountByBalanceBetween(BankAccountMapper.findByBalanceRequestDtoFromGrpc(req))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnEach(v -> spanTag("req", req.toString()))
                        .map(bankAccount -> GetAllByBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnNext(response -> log.info("response: {}", response.getBankAccount()));
    }

    @Override
    @NewSpan
    public Mono<GetAllByBalanceWithPaginationResponse> getAllByBalanceWithPagination(Mono<GetAllByBalanceWithPaginationRequest> request) {
        return request.flatMap(req -> bankAccountService.findAllBankAccountsByBalance(BankAccountMapper.findByBalanceRequestDtoFromGrpc(req))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnEach(v -> spanTag("req", req.toString()))
                        .map(BankAccountMapper::toPaginationGrpcResponse))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnNext(response -> log.info("response: {}", response.toString()));
    }

    private <T> T validate(T data) {
        var errors = validator.validate(data);
        if (!errors.isEmpty()) throw new ConstraintViolationException(errors);
        return data;
    }

    private void spanTag(String key, String value) {
        var span = tracer.currentSpan();
        if (span != null) span.tag(key, value);
    }

    private void spanError(Throwable ex) {
        var span = tracer.currentSpan();
        if (span != null) span.error(ex);
    }
}
