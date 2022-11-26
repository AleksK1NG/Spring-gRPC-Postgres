package com.alexander.bryksin.microservive.springwebfluxgrpc.dto;

import java.time.LocalDateTime;

public record ErrorHttpResponseDto(
        int Status,
        String message,
        LocalDateTime timestamp) {
}
