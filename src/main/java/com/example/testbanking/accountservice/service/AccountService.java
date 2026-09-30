package com.example.testbanking.accountservice.service;

import com.example.testbanking.common.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(Long userId, CreateAccountRequest request);
    AccountResponse getAccountByNumber(Long userId, String accountNumber);
    List<AccountResponse> getUserAccounts(Long userId);
    BalanceResponse getBalance(Long userId, String accountNumber);
    TransactionResponse transfer(Long userId, TransferRequest request, String idempotencyKey);
    TransactionResponse deposit(Long userId, DepositRequest request, String idempotencyKey);
    TransactionResponse withdraw(Long userId, WithdrawRequest request, String idempotencyKey);
}
