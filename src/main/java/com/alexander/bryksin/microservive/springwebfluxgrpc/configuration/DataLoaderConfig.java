package com.alexander.bryksin.microservive.springwebfluxgrpc.configuration;


import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.Currency;
import com.alexander.bryksin.microservive.springwebfluxgrpc.services.BankAccountService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "faker", name = "enable")
@RequiredArgsConstructor
public class DataLoaderConfig implements CommandLineRunner {

    private final BankAccountService bankAccountService;
    private final Faker faker;

    @Value(value = "${faker.count:1000}")
    private Integer count = 1000;


    @Override
    public void run(String... args) {
        Flux.range(0, count)
                .flatMap(v -> {
                    var bankAccount = BankAccount.builder()
                            .email(faker.internet().emailAddress())
                            .firstName(faker.name().firstName())
                            .lastName(faker.name().lastName())
                            .address(faker.address().fullAddress())
                            .phone(faker.phoneNumber().cellPhone())
                            .balance(BigDecimal.valueOf(faker.number().numberBetween(0, 500000)))
                            .currency(Currency.USD)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return bankAccountService.createBankAccount(bankAccount);
                })
                .doOnNext(bankAccount -> log.info("created bank account: {}", bankAccount))
                .doOnError(ex -> log.error("DataLoaderConfig error: {}", ex.getLocalizedMessage()))
                .doFinally(signalType -> log.info("mock data inserted successfully"))
                .subscribe();
    }
}
