package com.alexander.bryksin.microservive.springwebfluxgrpc.repositories;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
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
import java.util.Optional;

import static com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount.BALANCE;


@Slf4j
@Repository
@RequiredArgsConstructor
public class BankAccountPostgresRepositoryImpl implements BankAccountPostgresRepository {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate template;
    private final Tracer tracer;

    @Override
    @NewSpan
    public Mono<Page<BankAccount>> findAllBankAccountsByBalance(@SpanTag(key = "min") BigDecimal min,
                                                                @SpanTag(key = "max") BigDecimal max,
                                                                @SpanTag(key = "pageable") Pageable pageable) {

        var query = Query.query(Criteria.where(BALANCE).between(min, max)).with(pageable);

        var listMono = template.select(query, BankAccount.class).collectList()
                .doOnError(this::spanError)
                .doOnSuccess(list -> spanTag("list", String.valueOf(list.size())));

        var totalCountMono = databaseClient.sql("SELECT count(bank_account_id) as total FROM microservices.bank_accounts WHERE balance BETWEEN :min AND :max")
                .bind("min", min)
                .bind("max", max)
                .fetch()
                .one()
                .doOnError(this::spanError)
                .doOnSuccess(totalCount -> spanTag("totalCount", totalCount.toString()));

        return Mono.zip(listMono, totalCountMono).map(tuple -> new PageImpl<>(tuple.getT1(), pageable, (Long) tuple.getT2().get("total")));
    }


    private void spanTag(String key, String value) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.tag(key, value));
    }

    private void spanError(Throwable ex) {
        Optional.ofNullable(tracer.currentSpan()).ifPresent(span -> span.error(ex));
    }
}
