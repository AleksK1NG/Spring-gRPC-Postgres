package com.alexander.bryksin.microservive.springwebfluxgrpc.mappers;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.BankAccount;
import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.Currency;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.BankAccountSuccessResponseDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.CreateBankAccountDto;
import com.alexander.bryksin.microservive.springwebfluxgrpc.dto.FindByBalanceRequestDto;
import com.grpc.bankService.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class BankAccountMapper {
    private BankAccountMapper() {
    }

    public static BankAccount of(CreateBankAccountRequest req) {
        return BankAccount.builder()
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .currency(Currency.valueOf(req.getCurrency()))
                .balance(BigDecimal.valueOf(req.getBalance()))
                .phone(req.getPhone())
                .address(req.getAddress())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BankAccountData toGrpc(BankAccount bankAccount) {
        return BankAccountData.newBuilder()
                .setId(bankAccount.getId().toString())
                .setEmail(bankAccount.getEmail())
                .setFirstName(bankAccount.getFirstName())
                .setLastName(bankAccount.getLastName())
                .setBalance(bankAccount.getBalance().doubleValue())
                .setCurrency(bankAccount.getCurrency().name())
                .setAddress(bankAccount.getAddress())
                .setPhone(bankAccount.getPhone())
                .setCreatedAt(bankAccount.getCreatedAt().toString())
                .setUpdatedAt(bankAccount.getUpdatedAt().toString())
                .build();
    }

    public static GetAllByBalanceWithPaginationResponse toPaginationGrpcResponse(Page<BankAccount> page) {
        return GetAllByBalanceWithPaginationResponse.newBuilder()
                .addAllBankAccount(page.get().map(BankAccountMapper::toGrpc).toList())
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getNumberOfElements())
                .setSize(page.getSize())
                .setPage(page.getNumber())
                .setIsLast(page.isLast())
                .setIsFirst(page.isFirst())
                .build();
    }

    public static BankAccountSuccessResponseDto toSuccessHttpResponse(BankAccount bankAccount) {
        return new BankAccountSuccessResponseDto(
                bankAccount.getId().toString(),
                bankAccount.getEmail(),
                bankAccount.getFirstName(),
                bankAccount.getLastName(),
                bankAccount.getAddress(),
                bankAccount.getPhone(),
                bankAccount.getCurrency(),
                bankAccount.getBalance(),
                bankAccount.getCreatedAt(),
                bankAccount.getUpdatedAt());
    }

    public static BankAccount fromCreateBankAccountDto(CreateBankAccountDto createBankAccountDto) {
        return BankAccount.builder()
                .email(createBankAccountDto.email())
                .firstName(createBankAccountDto.firstName())
                .lastName(createBankAccountDto.lastName())
                .currency(createBankAccountDto.currency())
                .balance(createBankAccountDto.balance())
                .phone(createBankAccountDto.phone())
                .address(createBankAccountDto.address())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static FindByBalanceRequestDto findByBalanceRequestDtoFromGrpc(GetAllByBalanceRequest req) {
        return new FindByBalanceRequestDto(
                BigDecimal.valueOf(req.getMin()),
                BigDecimal.valueOf(req.getMax()),
                PageRequest.of(req.getPage(), req.getSize())
        );
    }

    public static FindByBalanceRequestDto findByBalanceRequestDtoFromGrpc(GetAllByBalanceWithPaginationRequest req) {
        return new FindByBalanceRequestDto(
                BigDecimal.valueOf(req.getMin()),
                BigDecimal.valueOf(req.getMax()),
                PageRequest.of(req.getPage(), req.getSize())
        );
    }
}
