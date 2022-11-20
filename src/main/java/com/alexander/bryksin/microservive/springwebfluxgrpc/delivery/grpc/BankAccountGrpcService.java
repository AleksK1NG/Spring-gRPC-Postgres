package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.grpc;

import com.alexander.bryksin.microservive.springwebfluxgrpc.mappers.BankAccountMapper;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import com.grpc.bankService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {

    private final BankAccountService bankAccountService;
    private static final Long timeoutMillis = 5000L;

    @Override
    public Mono<CreateBankAccountResponse> createBankAccount(Mono<CreateBankAccountRequest> request) {
        return request.flatMap(req -> bankAccountService.createBankAccount(BankAccountMapper.of(req)))
                .publishOn(Schedulers.boundedElastic())
                .map(bankAccount -> CreateBankAccountResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(ex -> log.error("error save account", ex))
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    @Override
    public Mono<GetBankAccountByIdResponse> getBankAccountById(Mono<GetBankAccountByIdRequest> request) {
        return request.flatMap(req -> bankAccountService.getBankAccountById(UUID.fromString(req.getId()))
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(bankAccount -> log.info("bank account: {}", bankAccount))
                        .doOnSuccess(result -> log.info("result: {}", result.toString()))
                        .doOnError(ex -> log.error("getBankAccountById", ex))
                        .map(bankAccount -> GetBankAccountByIdResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<DepositBalanceResponse> depositBalance(Mono<DepositBalanceRequest> request) {
        return request
                .flatMap(req -> bankAccountService.depositAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance()))
                        .map(bankAccount -> DepositBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<WithdrawBalanceResponse> withdrawBalance(Mono<WithdrawBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.withdrawAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance()))
                        .publishOn(Schedulers.boundedElastic())
                        .map(bankAccount -> WithdrawBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Flux<GetAllByBalanceResponse> getAllByBalance(Mono<GetAllByBalanceRequest> request) {
        return request
                .flatMapMany(req -> bankAccountService.findBankAccountByBalanceBetween(BigDecimal.valueOf(req.getMin()), BigDecimal.valueOf(req.getMax()), PageRequest.of(req.getPage(), req.getSize()))
                        .publishOn(Schedulers.boundedElastic())
                        .map(bankAccount -> GetAllByBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnNext(response -> log.info("response: {}", response.getBankAccount()));
    }

    @Override
    public Mono<GetAllByBalanceWithPaginationResponse> getAllByBalanceWithPagination(Mono<GetAllByBalanceWithPaginationRequest> request) {
        return request.flatMap(req -> bankAccountService.findAllBankAccountsByBalance(BigDecimal.valueOf(req.getMin()), BigDecimal.valueOf(req.getMax()), PageRequest.of(req.getPage(), req.getSize()))
                        .publishOn(Schedulers.boundedElastic())
                        .map(BankAccountMapper::toPaginationGrpcResponse))
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnNext(response -> log.info("response: {}", response.toString()));
    }
}
