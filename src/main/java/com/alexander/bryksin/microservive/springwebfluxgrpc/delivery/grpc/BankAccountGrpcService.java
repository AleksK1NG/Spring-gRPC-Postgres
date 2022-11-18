package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.grpc;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import com.grpc.bankService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {

    private final BankAccountService bankAccountService;

    @Override
    public Mono<CreateBankAccountResponse> createBankAccount(Mono<CreateBankAccountRequest> request) {
        return request.flatMap(req -> {
                    var bankAccount = BankAccount.builder()
                            .email(req.getEmail())
                            .firstName(req.getFirstName())
                            .lastName(req.getLastName())
                            .currency(req.getCurrency())
                            .balance(BigDecimal.valueOf(req.getBalance()))
                            .phone(req.getPhone())
                            .address(req.getAddress())
                            .updatedAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return bankAccountService.createBankAccount(bankAccount);
                })
                .map(bankAccount -> {
                    var createdBankAccount = BankAccountData.newBuilder()
                            .setEmail(bankAccount.getEmail())
                            .setFirstName(bankAccount.getFirstName())
                            .setLastName(bankAccount.getLastName())
                            .setBalance(bankAccount.getBalance().doubleValue())
                            .setCurrency(bankAccount.getCurrency())
                            .setAddress(bankAccount.getAddress())
                            .setPhone(bankAccount.getPhone())
                            .setCreatedAt(bankAccount.getCreatedAt().toString())
                            .setUpdatedAt(bankAccount.getUpdatedAt().toString())
                            .build();
                    return CreateBankAccountResponse.newBuilder().setBankAccount(createdBankAccount).build();
                })
                .doOnError(ex -> log.error("error save account", ex))
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    @Override
    public Mono<GetBankAccountByIdResponse> getBankAccountById(Mono<GetBankAccountByIdRequest> request) {
        return request.flatMap(req -> bankAccountService.getBankAccountById(UUID.fromString(req.getId()))
                        .doOnNext(bankAccount -> log.info("bank account: {}", bankAccount))
                        .doOnSuccess(result -> log.info("result: {}", result.toString()))
                        .doOnError(ex -> log.error("ex", ex))
                        .map(v -> GetBankAccountByIdResponse.newBuilder()
                                .setBankAccount(BankAccountData.newBuilder()
                                        .setEmail(v.getEmail())
                                        .setFirstName(v.getFirstName())
                                        .setLastName(v.getLastName())
                                        .setAddress(v.getAddress())
                                        .setCurrency(v.getCurrency())
                                        .setBalance(v.getBalance().doubleValue())
                                        .setPhone(v.getPhone())
                                        .setId(v.getId().toString())
                                        .setUpdatedAt(v.getUpdatedAt().toString())
                                        .setCreatedAt(v.getCreatedAt().toString())
                                        .build())
                                .build()))
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<DepositBalanceResponse> depositBalance(Mono<DepositBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.depositAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance())))
                .map(v -> DepositBalanceResponse.newBuilder()
                        .setBankAccount(BankAccountData.newBuilder()
                                .setEmail(v.getEmail())
                                .setFirstName(v.getFirstName())
                                .setLastName(v.getLastName())
                                .setAddress(v.getAddress())
                                .setCurrency(v.getCurrency())
                                .setBalance(v.getBalance().doubleValue())
                                .setPhone(v.getPhone())
                                .setId(v.getId().toString())
                                .setUpdatedAt(v.getUpdatedAt().toString())
                                .setCreatedAt(v.getCreatedAt().toString())
                                .build())
                        .build())
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Mono<WithdrawBalanceResponse> withdrawBalance(Mono<WithdrawBalanceRequest> request) {
        return request.flatMap(req -> bankAccountService.withdrawAmount(UUID.fromString(req.getId()), BigDecimal.valueOf(req.getBalance())))
                .map(v -> WithdrawBalanceResponse.newBuilder()
                        .setBankAccount(BankAccountData.newBuilder()
                                .setEmail(v.getEmail())
                                .setFirstName(v.getFirstName())
                                .setLastName(v.getLastName())
                                .setAddress(v.getAddress())
                                .setCurrency(v.getCurrency())
                                .setBalance(v.getBalance().doubleValue())
                                .setPhone(v.getPhone())
                                .setId(v.getId().toString())
                                .setUpdatedAt(v.getUpdatedAt().toString())
                                .setCreatedAt(v.getCreatedAt().toString())
                                .build())
                        .build())
                .doOnSuccess(response -> log.info("response: {}", response.toString()));
    }

    @Override
    public Flux<GetAllByBalanceResponse> getAllByBalance(Mono<GetAllByBalanceRequest> request) {
        return super.getAllByBalance(request);
    }

    @Override
    public Mono<GetAllByBalanceWithPaginationResponse> getAllByBalanceWithPagination(Mono<GetAllByBalanceWithPaginationRequest> request) {
        return super.getAllByBalanceWithPagination(request);
    }

}
