package com.example.expense.DTO;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionDTO {

    private Long accountId; // Счет, к которому относится транзакция

    private OperationType type; // Тип транзакции (INCOME, EXPENSE)

    private Long categoryId = null;

    private BigDecimal amount; // Сумма транзакции

    private String description; // Описание транзакции

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean adjustBalance = false;

}
