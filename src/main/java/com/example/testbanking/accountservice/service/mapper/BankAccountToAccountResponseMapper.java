package com.example.testbanking.accountservice.service.mapper;

import com.example.testbanking.common.dto.AccountResponse;
import com.example.testbanking.accountservice.entity.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class BankAccountToAccountResponseMapper {

    public AccountResponse map(final BankAccount account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .userId(account.getUserId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
