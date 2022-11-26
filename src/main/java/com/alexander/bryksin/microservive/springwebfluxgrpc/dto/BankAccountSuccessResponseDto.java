package com.alexander.bryksin.microservive.springwebfluxgrpc.dto;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankAccountSuccessResponseDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String address,
        String phone,
        Currency currency,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
