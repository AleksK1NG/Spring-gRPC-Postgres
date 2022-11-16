package com.alexander.bryksin.microservive.springwebfluxgrpc;

import com.grpc.bankService.BankAccount;
import com.grpc.bankService.ReactorBankAccountServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringWebfluxGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxGrpcApplication.class, args);
    }
}


@GrpcService
@Slf4j
class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {
    @Override
    public Mono<BankAccount.CreateBankAccountResponse> createBankAccount(Mono<BankAccount.CreateBankAccountRequest> request) {
        return request.map(req -> BankAccount.CreateBankAccountResponse.newBuilder()
                .setBankAccount(BankAccount.BankAccountData.newBuilder()
                        .setEmail(req.getEmail())
                        .build())
                .build()).doOnSuccess(value -> log.info("success response: {}", value.toString()));
    }

    @Override
    public Mono<BankAccount.GetBankAccountByIdResponse> getBankAccountById(Mono<BankAccount.GetBankAccountByIdRequest> request) {
        return super.getBankAccountById(request);
    }

    @Override
    public Mono<BankAccount.DepositBalanceResponse> depositBalance(Mono<BankAccount.DepositBalanceRequest> request) {
        return super.depositBalance(request);
    }

    @Override
    public Mono<BankAccount.WithdrawBalanceResponse> withdrawBalance(Mono<BankAccount.WithdrawBalanceRequest> request) {
        return super.withdrawBalance(request);
    }

    @Override
    public Flux<BankAccount.GetAllByBalanceResponse> getAllByBalance(Mono<BankAccount.GetAllByBalanceRequest> request) {
        return super.getAllByBalance(request);
    }

    @Override
    public Mono<BankAccount.GetAllByBalanceWithPaginationResponse> getAllByBalanceWithPagination(Mono<BankAccount.GetAllByBalanceWithPaginationRequest> request) {
        return super.getAllByBalanceWithPagination(request);
    }
}



