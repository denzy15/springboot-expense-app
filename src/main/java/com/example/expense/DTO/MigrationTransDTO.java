package com.example.expense.DTO;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class MigrationTransDTO {
    private LocalDate trDate;
    private Long budgetId;
    private BigDecimal totalAmount;
    private OperationType trType;
    private Long categoryId;
    private int trCount;
}
