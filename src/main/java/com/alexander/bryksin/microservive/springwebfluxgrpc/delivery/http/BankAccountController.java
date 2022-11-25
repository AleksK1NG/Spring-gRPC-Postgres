package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.http;


import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.BankAccountSuccessResponseDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.CreateBankAccountDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.DepositBalanceDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.WithdrawBalanceDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.mappers.BankAccountMapper;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/bank")
@Slf4j
@RequiredArgsConstructor
@Tags(@Tag(name = "Bank Accounts", description = "Bank Account REST Controller"))
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private static final Long timeoutMillis = 5000L;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "createBankAccount",
            summary = "Create bew bank account",
            operationId = "createBankAccount",
            description = "Create new bank for account for user")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> createBankAccount(@RequestBody CreateBankAccountDto createBankAccountDto) {
        return bankAccountService.createBankAccount(BankAccountMapper.fromCreateBankAccountDto(createBankAccountDto))
                .map(bankAccount -> ResponseEntity.status(HttpStatus.CREATED).body(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("created bank account: {}", response.getBody()));
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "getBankAccountById",
            summary = "Get bank account by id",
            operationId = "getBankAccountById",
            description = "Get user bank account by given id")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> getBankAccountById(@PathVariable String id) {
        return bankAccountService.getBankAccountById(UUID.fromString(id))
                .map(bankAccount -> ResponseEntity.ok(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("get bank account by id response: {}", response.getBody()));
    }

    @PutMapping(path = "/deposit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "depositBalance",
            summary = "Deposit balance",
            operationId = "depositBalance",
            description = "Deposit given amount to the bank account balance")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> depositBalance(@RequestBody DepositBalanceDto depositBalanceDto, @PathVariable UUID id) {
        return bankAccountService.depositAmount(id, depositBalanceDto.amount())
                .map(bankAccount -> ResponseEntity.ok(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.getBody()));
    }

    @PutMapping(path = "/withdraw/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "withdrawBalance",
            summary = "Withdraw balance",
            operationId = "withdrawBalance",
            description = "Withdraw given amount from the bank account balance")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> withdrawBalance(@RequestBody WithdrawBalanceDto withdrawBalanceDto, @PathVariable UUID id) {
        return bankAccountService.withdrawAmount(id, withdrawBalanceDto.amount())
                .map(bankAccount -> ResponseEntity.ok(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.getBody()));
    }

    @GetMapping(path = "all/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "findAllAccountsByBalance",
            summary = "Find all bank account with given amount range",
            operationId = "findAllAccounts",
            description = "Find all bank accounts for the given balance range with pagination")
    public Mono<ResponseEntity<Page<BankAccountSuccessResponseDto>>> getByBalanceRange(
            @RequestParam(name = "min", defaultValue = "0") BigDecimal min,
            @RequestParam(name = "max", defaultValue = "500000000") BigDecimal max,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bankAccountService.findAllBankAccountsByBalance(min, max, PageRequest.of(page, size))
                .map(bankAccount -> ResponseEntity.ok(bankAccount.map(BankAccountMapper::toSuccessHttpResponse)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnSuccess(response -> log.info("response: {}", response.getBody()));
    }

    @GetMapping(path = "all/balance/stream")
    @Operation(
            method = "getAllByBalanceStream",
            summary = "Find all bank account with given amount range returns stream",
            operationId = "getAllByBalanceStream",
            description = "Find all bank accounts for the given balance range")
    public Flux<BankAccountSuccessResponseDto> getByBalanceRangeStream(
            @RequestParam(name = "min", defaultValue = "0") BigDecimal min,
            @RequestParam(name = "max", defaultValue = "500000000") BigDecimal max,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bankAccountService.findBankAccountByBalanceBetween(min, max, PageRequest.of(page, size))
                .map(BankAccountMapper::toSuccessHttpResponse)
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnNext(response -> log.info("response: {}", response));
    }

}
