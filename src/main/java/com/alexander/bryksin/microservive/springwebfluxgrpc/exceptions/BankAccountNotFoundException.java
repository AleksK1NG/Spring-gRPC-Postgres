package com.alexander.bryksin.microservive.springwebfluxgrpc.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String id) {
        super(String.format("bank account with id %s not found", id));
    }

    public BankAccountNotFoundException(String id, Throwable cause) {
        super(String.format("bank account with id %s not found", id), cause);
    }
}
