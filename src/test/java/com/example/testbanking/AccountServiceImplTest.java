package com.example.testbanking;

import com.example.testbanking.accountservice.entity.BankAccount;
import com.example.testbanking.accountservice.entity.enums.Currency;
import com.example.testbanking.accountservice.repository.BankAccountRepository;
import com.example.testbanking.accountservice.service.AccountServiceImpl;
import com.example.testbanking.common.dto.BalanceResponse;
import com.example.testbanking.common.exception.BankingException;
import com.example.testbanking.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private final Long ownerUserId = 1L;
    private final Long strangerUserId = 99L;
    private final String accountNumber = "ACC-12345678";
    private BankAccount mockAccount;

    @BeforeEach
    void setUp() {
        mockAccount = BankAccount.builder()
                .id(10L)
                .userId(ownerUserId)
                .accountNumber(accountNumber)
                .balance(new BigDecimal("500.00"))
                .currency(Currency.USD)
                .build();
    }

    @Test
    @DisplayName("getBalance - Success when user owns the account")
    void getBalance_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(mockAccount));

        // Act
        BalanceResponse response = accountService.getBalance(ownerUserId, accountNumber);

        // Assert
        assertNotNull(response);
        assertEquals(accountNumber, response.getAccountNumber());
        assertEquals(new BigDecimal("500.00"), response.getBalance());
        assertNotNull(response.getTimestamp());

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("getBalance - Throws ACCOUNT_NOT_FOUND when account does not exist")
    void getBalance_AccountNotFound_ThrowsException() {
        // Arrange
        String unknownAcc = "ACC-UNKNOWN";
        when(accountRepository.findByAccountNumber(unknownAcc))
                .thenReturn(Optional.empty());

        // Act & Assert
        BankingException exception = assertThrows(
                BankingException.class,
                () -> accountService.getBalance(ownerUserId, unknownAcc)
        );

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository, times(1)).findByAccountNumber(unknownAcc);
    }

    @Test
    @DisplayName("getBalance - Throws ACCESS_DENIED when user does not own the account")
    void getBalance_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(mockAccount));

        // Act & Assert
        BankingException exception = assertThrows(
                BankingException.class,
                () -> accountService.getBalance(strangerUserId, accountNumber)
        );

        assertEquals(ErrorCode.ACCOUNT_ACCESS_DENIED, exception.getErrorCode());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }
}
