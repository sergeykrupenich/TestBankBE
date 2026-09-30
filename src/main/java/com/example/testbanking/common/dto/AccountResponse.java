package com.example.testbanking.common.dto;

import com.example.testbanking.accountservice.entity.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
}
