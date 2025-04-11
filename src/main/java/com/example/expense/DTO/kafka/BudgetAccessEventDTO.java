package com.example.expense.DTO.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BudgetAccessEventDTO {
    private Long budgetId;
    private Long userId;
}
