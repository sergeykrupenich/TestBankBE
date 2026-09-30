package com.example.testbanking.common;

import com.example.testbanking.common.exception.BankingException;
import com.example.testbanking.common.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";

    @ExceptionHandler(BankingException.class)
    public ResponseEntity<Map<String, Object>> handleBankingException(BankingException ex) {
        final ErrorCode errorCode = ex.getErrorCode();
        final Map<String, Object> responseBody = createResponseBody(errorCode, ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(responseBody);
    }

    private Map<String, Object> createResponseBody(final ErrorCode errorCode, final BankingException ex) {
        return Map.of(
                TIMESTAMP_KEY, LocalDateTime.now(),
                CODE_KEY, errorCode.getCode(),
                MESSAGE_KEY, ex.getMessage(),
                STATUS_KEY, errorCode.getHttpStatus().value()
        );
    }
}
