package com.example.expense.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class AccountBalanceTransferRequestDTO {
    Long accountIdFrom;
    Long accountIdTo;
    BigDecimal amount;
}
