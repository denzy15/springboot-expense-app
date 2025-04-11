package com.example.expense.DTO.kafka;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class TransactionEventDTO {
    private BigDecimal oldAmount = null;
    private BigDecimal newAmount;
    private OperationType type;
    private Long budgetId;
    private Long oldCategoryId = null;
    private Long newCategoryId;
    private LocalDateTime createdAt;
}

