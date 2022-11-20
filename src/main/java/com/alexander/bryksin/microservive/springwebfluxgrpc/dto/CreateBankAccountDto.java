package com.alexander.bryksin.microservive.springwebfluxgrpc.dto;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.Currency;

import java.math.BigDecimal;

public record CreateBankAccountDto(String email,
                                   String firstName,
                                   String lastName,
                                   String address,
                                   String phone,
                                   Currency currency,
                                   BigDecimal balance
) {
}
