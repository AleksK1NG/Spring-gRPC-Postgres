package com.alexander.bryksin.microservive.springwebfluxgrpc.repositories;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount.BALANCE;


@Repository
@RequiredArgsConstructor
@Slf4j
public class BankAccountPostgresRepositoryImpl implements BankAccountPostgresRepository {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Page<BankAccount>> findAllBankAccountsByBalance(BigDecimal min, BigDecimal max, Pageable pageable) {
        var query = Query.query(Criteria.where(BALANCE).between(min, max));
        Mono<List<BankAccount>> listMono = template.select(query, BankAccount.class).collectList();

        Mono<Map<String, Object>> totalCountMono = databaseClient.sql("SELECT count(bank_account_id) as total FROM microservices.bank_accounts WHERE balance BETWEEN :min AND :max")
                .bind("min", min)
                .bind("max", max)
                .fetch()
                .one();

        return Mono.zip(listMono, totalCountMono).map(tuple -> new PageImpl<>(tuple.getT1(), pageable, (Long) tuple.getT2().get("total")));
    }
}
