package com.example.testbanking.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth & User Errors
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User with this email already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "User was not found", HttpStatus.NOT_FOUND),

    // Account Operation Errors
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Account not found: %s", HttpStatus.NOT_FOUND),
    SAME_ACCOUNT_TRANSFER("SAME_ACCOUNT_TRANSFER", "Cannot transfer money to the same account", HttpStatus.BAD_REQUEST),
    CROSS_CURRENCY_NOT_SUPPORTED("CROSS_CURRENCY_NOT_SUPPORTED", "Multi-currency transfers are not supported yet", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS", "Insufficient funds on the account", HttpStatus.BAD_REQUEST),

    // Access & Security Errors
    ACCOUNT_ACCESS_DENIED("ACCOUNT_ACCESS_DENIED", "You are not the owner of this account", HttpStatus.FORBIDDEN),
    DEPOSIT_ACCESS_DENIED("DEPOSIT_ACCESS_DENIED", "You cannot deposit into someone else's account", HttpStatus.FORBIDDEN);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String format(Object... args) {
        return String.format(messageTemplate, args);
    }
}
