package com.alexander.bryksin.microservive.springwebfluxgrpc.delivery.grpc;


import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.BankAccountNotFoundException;
import com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions.InvalidAmountException;
import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;

@GrpcAdvice
@Slf4j
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(RuntimeException.class)
    public StatusException handleRuntimeException(RuntimeException ex) {
        var status = Status.INTERNAL.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) RuntimeException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(BankAccountNotFoundException.class)
    public StatusException handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        var status = Status.NOT_FOUND.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) BankAccountNotFoundException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(InvalidAmountException.class)
    public StatusException handleInvalidAmountException(InvalidAmountException ex) {
        var status = Status.INVALID_ARGUMENT.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) InvalidAmountException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(DataAccessException.class)
    public StatusException handleDataAccessException(DataAccessException ex) {
        var status = Status.INVALID_ARGUMENT.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) DataAccessException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(ConstraintViolationException.class)
    public StatusException handleConstraintViolationException(ConstraintViolationException ex) {
        var status = Status.INVALID_ARGUMENT.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) ConstraintViolationException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(MethodArgumentNotValidException.class)
    public StatusException handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var status = Status.INVALID_ARGUMENT.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) MethodArgumentNotValidException: ", ex);
        return status.asException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusException handleIllegalArgumentException(IllegalArgumentException ex) {
        var status = Status.INVALID_ARGUMENT.withDescription(ex.getLocalizedMessage()).withCause(ex);
        log.error("(GrpcExceptionAdvice) IllegalArgumentException: ", ex);
        return status.asException();
    }
}
