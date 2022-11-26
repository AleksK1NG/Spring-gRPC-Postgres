package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.http;


import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.*;
import com.alexander.bryksin.microservive.springwebfluxgrpc.mappers.BankAccountMapper;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/bank")
@Slf4j
@RequiredArgsConstructor
@Tags(@Tag(name = "Bank Accounts", description = "Bank Account REST Controller"))
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private static final Long timeoutMillis = 5000L;
    private final Tracer tracer;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "createBankAccount",
            summary = "Create bew bank account",
            operationId = "createBankAccount",
            description = "Create new bank for account for user")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> createBankAccount(@RequestBody CreateBankAccountDto createBankAccountDto) {
        return bankAccountService.createBankAccount(BankAccountMapper.fromCreateBankAccountDto(createBankAccountDto))
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .map(bankAccount -> ResponseEntity.status(HttpStatus.CREATED).body(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(res -> log.info("response: status: {}, body: {}", spanTagResponseEntity(res).getStatusCodeValue(), res.getBody()));
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            method = "getBankAccountById",
            summary = "Get bank account by id",
            operationId = "getBankAccountById",
            description = "Get user bank account by given id")
    public Mono<ResponseEntity<BankAccountSuccessResponseDto>> getBankAccountById(@PathVariable String id) {
        return bankAccountService.getBankAccountById(UUID.fromString(id))
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .map(bankAccount -> ResponseEntity.ok(BankAccountMapper.toSuccessHttpResponse(bankAccount)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(res -> log.info("response: status: {}, body: {}", spanTagResponseEntity(res).getStatusCodeValue(), res.getBody()));
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
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(res -> log.info("response: status: {}, body: {}", spanTagResponseEntity(res).getStatusCodeValue(), res.getBody()));
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
                .doOnNext(bankAccount -> spanTag("bankAccount", bankAccount.toString()))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(res -> log.info("response: status: {}, body: {}", spanTagResponseEntity(res).getStatusCodeValue(), res.getBody()));
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
        return bankAccountService.findAllBankAccountsByBalance(new FindByBalanceRequestDto(min, max, PageRequest.of(page, size)))
                .doOnNext(result -> spanTag("result", spanTagPageRequest(result)))
                .map(bankAccount -> ResponseEntity.ok(bankAccount.map(BankAccountMapper::toSuccessHttpResponse)))
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnSuccess(res -> log.info("response: status: {}, body: {}", spanTagResponseEntity(res).getStatusCodeValue(), res.getBody()));
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
        return bankAccountService.findBankAccountByBalanceBetween(new FindByBalanceRequestDto(min, max, PageRequest.of(page, size)))
                .map(BankAccountMapper::toSuccessHttpResponse)
                .publishOn(Schedulers.boundedElastic())
                .timeout(Duration.ofMillis(timeoutMillis))
                .doOnError(this::spanError)
                .doOnNext(response -> log.info("response: {}", response));
    }


    private void spanTag(String key, String value) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.tag(key, value));
    }

    private <T> ResponseEntity<T> spanTagResponseEntity(ResponseEntity<T> responseEntity) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> {
            span.tag("status", responseEntity.getStatusCode().toString());
            if (responseEntity.getBody() != null) span.tag("body", responseEntity.getBody().toString());
        });
        return responseEntity;
    }

    private void spanError(Throwable ex) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.error(ex));
    }

    private String spanTagPageRequest(Page<?> response) {
        return String.format("totalElements: %s, totalPages: %s, pagination: %s",
                response.getTotalElements(),
                response.getTotalPages(),
                response.getPageable());
    }
}
