package com.alexander.bryksin.microservive.springwebfluxgrpc.dto;

import com.alexander.bryksin.microservive.springwebfluxgrpc.domain.Currency;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateBankAccountDto(@Email String email,
                                   @Size(min = 3, max = 60) String firstName,
                                   @Size(min = 3, max = 60) String lastName,
                                   @Size(min = 3, max = 560) String address,
                                   @Size(min = 3, max = 60) String phone,
                                   Currency currency,
                                   @DecimalMin(value = "0.0") BigDecimal balance) {
}
