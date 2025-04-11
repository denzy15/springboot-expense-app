package com.example.expense.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class MigrationBudgetAccessDTO {
    private Long budgetId;
    private Long userId;
}
