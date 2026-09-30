package com.example.testbanking.common.dto;

import com.example.testbanking.accountservice.entity.enums.Currency;
import com.example.testbanking.accountservice.entity.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse{
    private Long transactionId;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private Currency currency;
    private TransactionStatus status;
    private LocalDateTime timestamp;
}
