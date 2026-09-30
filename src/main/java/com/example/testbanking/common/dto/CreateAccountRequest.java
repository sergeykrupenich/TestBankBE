package com.example.testbanking.common.dto;

import com.example.testbanking.accountservice.entity.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotNull(message = "{account.currency.required}")
    private Currency currency;
}
