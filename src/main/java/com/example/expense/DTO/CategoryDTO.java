package com.example.expense.DTO;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryDTO {
    private String name;
    private Long budgetId;
    private OperationType categoryType;
}
