package com.example.testbanking.accountservice.service;

import com.example.testbanking.accountservice.AccountConstants;
import com.example.testbanking.accountservice.entity.BankAccount;
import com.example.testbanking.accountservice.entity.Transaction;
import com.example.testbanking.accountservice.entity.enums.TransactionStatus;
import com.example.testbanking.accountservice.event.TransactionEventProducer;
import com.example.testbanking.accountservice.repository.BankAccountRepository;
import com.example.testbanking.accountservice.repository.TransactionRepository;
import com.example.testbanking.accountservice.service.mapper.BankAccountToAccountResponseMapper;
import com.example.testbanking.common.dto.*;
import com.example.testbanking.common.exception.BankingException;
import com.example.testbanking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;
    private final BankAccountToAccountResponseMapper bankAccountToAccountResponseMapper;

    private static final String ACC_PREFIX = "ACC-";

    @Override
    @Transactional
    @CachePut(value = "accounts", key = "#result.accountNumber")
    public AccountResponse createAccount(Long userId, CreateAccountRequest request) {
        final String accountNumber = ACC_PREFIX + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        final BankAccount account = BankAccount.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO.setScale(request.getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_EVEN))
                .build();

        final BankAccount saved = accountRepository.save(account);
        log.info("Bank account successfully created");

        return bankAccountToAccountResponseMapper.map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(Long userId, String accountNumber) {
        final BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankingException(ErrorCode.ACCOUNT_NOT_FOUND, accountNumber));

        validateAccountOwnership(account, userId);

        return bankAccountToAccountResponseMapper.map(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(Long userId) {
        return accountRepository.findAllByUserId(userId).stream()
                .map(bankAccountToAccountResponseMapper::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long userId, String accountNumber) {
        final BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankingException(ErrorCode.ACCOUNT_NOT_FOUND, accountNumber));

        validateAccountOwnership(account, userId);

        return new BalanceResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#request.sourceAccountNumber"),
            @CacheEvict(value = "accounts", key = "#request.targetAccountNumber")
    })
    public TransactionResponse transfer(Long userId, TransferRequest request, String idempotencyKey) {
        if (request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            throw new BankingException(ErrorCode.SAME_ACCOUNT_TRANSFER);
        }

        final String firstLockAcc = request.getSourceAccountNumber().compareTo(request.getTargetAccountNumber()) < 0
                ? request.getSourceAccountNumber() : request.getTargetAccountNumber();
        final String secondLockAcc = firstLockAcc.equals(request.getSourceAccountNumber())
                ? request.getTargetAccountNumber() : request.getSourceAccountNumber();

        final BankAccount firstAccount = accountRepository.findByAccountNumberWithLock(firstLockAcc)
                .orElseThrow(() -> new BankingException(ErrorCode.ACCOUNT_NOT_FOUND, firstLockAcc));
        final BankAccount secondAccount = accountRepository.findByAccountNumberWithLock(secondLockAcc)
                .orElseThrow(() -> new BankingException(ErrorCode.ACCOUNT_NOT_FOUND, secondLockAcc));

        final BankAccount source = firstAccount.getAccountNumber().equals(request.getSourceAccountNumber())
                ? firstAccount : secondAccount;
        final BankAccount target = firstAccount.getAccountNumber().equals(request.getTargetAccountNumber())
                ? firstAccount : secondAccount;

        validateAccountOwnership(source, userId);

        if (source.getCurrency() != target.getCurrency()) {
            throw new BankingException(ErrorCode.CROSS_CURRENCY_NOT_SUPPORTED);
        }

        final BigDecimal transferAmount = request.getAmount()
                .setScale(source.getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_EVEN);

        if (source.getBalance().compareTo(transferAmount) < 0) {
            throw new BankingException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        source.setBalance(source.getBalance().subtract(transferAmount));
        target.setBalance(target.getBalance().add(transferAmount));

        accountRepository.save(source);
        accountRepository.save(target);

        final Transaction transaction = Transaction.builder()
                .sourceAccountNumber(source.getAccountNumber())
                .targetAccountNumber(target.getAccountNumber())
                .amount(transferAmount)
                .currency(source.getCurrency())
                .status(TransactionStatus.SUCCESS)
                .build();
        final Transaction savedTx = transactionRepository.save(transaction);

        publishTransactionEvent(savedTx);

        return mapToTransactionResponse(savedTx);
    }

    @Override
    @Transactional
    @CacheEvict(value = "accounts", key = "#request.accountNumber")
    public TransactionResponse deposit(Long userId, DepositRequest request, String idempotencyKey) {
        final BankAccount account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new BankingException(ErrorCode.ACCOUNT_NOT_FOUND, request.getAccountNumber()));

        validateAccountOwnership(account, userId);

        final BigDecimal depositAmount = request.getAmount()
                .setScale(account.getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_EVEN);

        account.setBalance(account.getBalance().add(depositAmount));
        accountRepository.save(account);

        final Transaction transaction = Transaction.builder()
                .sourceAccountNumber(AccountConstants.SYSTEM_ACCOUNT_TOP_UP)
                .targetAccountNumber(account.getAccountNumber())
                .amount(depositAmount)
                .currency(account.getCurrency())
                .status(TransactionStatus.SUCCESS)
                .build();
        final Transaction savedTx = transactionRepository.save(transaction);

        publishTransactionEvent(savedTx);

        return mapToTransactionResponse(savedTx);
    }

    private void validateAccountOwnership(BankAccount account, Long userId) {
        if (!account.getUserId().equals(userId)) {
            throw new BankingException(ErrorCode.ACCOUNT_ACCESS_DENIED);
        }
    }

    private void publishTransactionEvent(Transaction tx) {
        final TransactionEvent event = TransactionEvent.builder()
                .transactionId(tx.getId())
                .sourceAccountNumber(tx.getSourceAccountNumber())
                .targetAccountNumber(tx.getTargetAccountNumber())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .status(tx.getStatus())
                .timestamp(tx.getTimestamp())
                .build();

        eventProducer.sendTransactionEvent(event);
    }

    private TransactionResponse mapToTransactionResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getSourceAccountNumber(),
                tx.getTargetAccountNumber(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getStatus(),
                tx.getTimestamp()
        );
    }
}
