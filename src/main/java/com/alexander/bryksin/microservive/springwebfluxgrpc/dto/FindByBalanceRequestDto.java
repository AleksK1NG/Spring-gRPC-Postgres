package com.alexander.bryksin.microservive.springwebfluxgrpc.dto;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public record FindByBalanceRequestDto(
        BigDecimal min,
        BigDecimal max,
        Pageable pageable) {
}
