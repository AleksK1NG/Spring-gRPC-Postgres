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
import java.util.UUID;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {

    private final BankAccountService bankAccountService;

    @Override
    public Mono<CreateBankAccountResponse> createBankAccount(Mono<CreateBankAccountRequest> request) {
        return request.flatMap(req -> bankAccountService.createBankAccount(BankAccountMapper.of(req)))
                .map(bankAccount -> CreateBankAccountResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .doOnError(ex -> log.error("error save account", ex))
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    @Override
    public Mono<GetBankAccountByIdResponse> getBankAccountById(Mono<GetBankAccountByIdRequest> request) {
        return request.flatMap(req -> bankAccountService.getBankAccountById(UUID.fromString(req.getId()))
                        .doOnNext(bankAccount -> log.info("bank account: {}", bankAccount))
                        .doOnSuccess(result -> log.info("result: {}", result.toString()))
                        .doOnError(ex -> log.error("ex", ex))
                        .map(bankAccount -> GetBankAccountByIdResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build()))
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<DepositBalanceResponse> depositBalance(Mono<DepositBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.depositAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance())))
                .map(bankAccount -> DepositBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<WithdrawBalanceResponse> withdrawBalance(Mono<WithdrawBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.withdrawAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance())))
                .map(bankAccount -> WithdrawBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Flux<GetAllByBalanceResponse> getAllByBalance(Mono<GetAllByBalanceRequest> request) {
        return request
                .publishOn(Schedulers.boundedElastic())
                .flatMapMany(req -> bankAccountService.findBankAccountByBalanceBetween(BigDecimal.valueOf(req.getMin()), BigDecimal.valueOf(req.getMax()), PageRequest.of(req.getPage(), req.getSize())))
                .map(bankAccount -> GetAllByBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.toGrpc(bankAccount)).build())
                .doOnNext(response -> log.info("response: {}", response.getBankAccount()));
    }

    @Override
    public Mono<GetAllByBalanceWithPaginationResponse> getAllByBalanceWithPagination(Mono<GetAllByBalanceWithPaginationRequest> request) {
        return super.getAllByBalanceWithPagination(request);
    }

}
