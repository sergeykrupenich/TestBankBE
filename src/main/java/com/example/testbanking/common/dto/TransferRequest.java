package com.example.testbanking.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "{transfer.source.required}")
    private String sourceAccountNumber;

    @NotBlank(message = "{transfer.target.required}")
    private String targetAccountNumber;

    @NotNull(message = "{transfer.amount.required}")
    @DecimalMin(value = "0.01", message = "{transfer.amount.min}")
    private BigDecimal amount;
}
