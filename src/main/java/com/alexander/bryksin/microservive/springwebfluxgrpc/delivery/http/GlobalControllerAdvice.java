package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.http;


import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.ErrorHttpResponseDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.BankAccountNotFoundException;
import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.InvalidAmountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Order(2)
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleRuntimeException(RuntimeException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) RuntimeException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }

    @ExceptionHandler(value = {BankAccountNotFoundException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.NOT_FOUND.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) BankAccountNotFoundException", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }

    @ExceptionHandler(value = {DataAccessException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleDataAccessException(DataAccessException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) DataAccessException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }

    @ExceptionHandler(value = {InvalidAmountException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleInvalidAmountException(InvalidAmountException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) InvalidAmountException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) InvalidAmountException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorHttpResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        var errorHttpResponseDto = new ErrorHttpResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), LocalDateTime.now());
        log.error("(GlobalControllerAdvice) ConstraintViolationException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorHttpResponseDto);
    }
}
