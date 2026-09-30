package com.example.testbanking.accountservice.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    BYN("933", 2, "Br", "BYN"),
    USD("840", 2, "$", "USD"),
    EUR("978", 2, "€", "EUR"),
    CNY("156", 2, "¥", "CNY");

    private final String numericCode;
    private final int defaultFractionDigits;
    private final String symbol;
    private final String displayName;
}
