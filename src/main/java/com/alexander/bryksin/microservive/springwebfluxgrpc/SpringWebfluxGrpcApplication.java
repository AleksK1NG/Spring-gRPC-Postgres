package com.alexander.bryksin.microservive.springwebfluxgrpc;

import com.grpc.bankService.*;
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
class BankAccountService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {
    @Override
    public Mono<CreateBankAccountResponse> createBankAccount(Mono<CreateBankAccountRequest> request) {
        return request.map(req -> CreateBankAccountResponse.newBuilder()
                        .setBankAccount(BankAccountData.newBuilder()
                                .setEmail(req.getEmail())
                                .setFirstName(req.getFirstName())
                                .setLastName(req.getLastName())
                                .setCurrency(req.getCurrency())
                                .setBalance(req.getBalance())
                                .build())
                        .build())
                .doOnSuccess(result -> log.info("result: {}", result.toString()));
    }

    @Override
    public Mono<GetBankAccountByIdResponse> getBankAccountById(Mono<GetBankAccountByIdRequest> request) {
        return super.getBankAccountById(request);
    }

    @Override
    public Mono<DepositBalanceResponse> depositBalance(Mono<DepositBalanceRequest> request) {
        return super.depositBalance(request);
    }

    @Override
    public Mono<WithdrawBalanceResponse> withdrawBalance(Mono<WithdrawBalanceRequest> request) {
        return super.withdrawBalance(request);
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

//
//@GrpcService
//@Slf4j
//class BankAccountGrpcService extends ReactorBankAccountServiceGrpc.BankAccountServiceImplBase {
//
//}



