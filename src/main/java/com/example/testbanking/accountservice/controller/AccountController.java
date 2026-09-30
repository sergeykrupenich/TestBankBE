package com.example.testbanking.accountservice.controller;

import com.example.testbanking.accountservice.service.security.CustomUserDetails;
import com.example.testbanking.common.dto.*;
import com.example.testbanking.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateAccountRequest request) {
        log.info("Processing account creation request");
        final AccountResponse response = accountService.createAccount(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("Request received: fetch user accounts");
        return ResponseEntity.ok(accountService.getUserAccounts(user.getId()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String accountNumber) {
        log.info("Request received: fetch account details");
        return ResponseEntity.ok(accountService.getAccountByNumber(user.getId(), accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String accountNumber) {
        log.info("Request received: fetch account balance");
        return ResponseEntity.ok(accountService.getBalance(user.getId(), accountNumber));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {
        log.info("Request received: process transfer");
        final TransactionResponse response = accountService.transfer(user.getId(), request, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody DepositRequest request) {
        log.info("Request received: process deposit");
        final TransactionResponse response = accountService.deposit(user.getId(), request, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody WithdrawRequest request) {

        TransactionResponse response = accountService.withdraw(user.getId(), request, idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
