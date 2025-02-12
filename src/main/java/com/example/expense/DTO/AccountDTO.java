package com.example.expense.DTO;


import com.example.expense.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountDTO {
    private String name;
    private Long budgetId;
    private BigDecimal balance;
}
