package com.example.testbanking.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotBlank(message = "{account.number.required}")
    private String accountNumber;

    @NotNull(message = "{account.deposit.amount.required}")
    @DecimalMin(value = "0.01", message = "{account.deposit.amount.min}")
    private BigDecimal amount;
}
