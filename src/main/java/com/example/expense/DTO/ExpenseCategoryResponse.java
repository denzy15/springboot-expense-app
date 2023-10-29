package com.example.expense.DTO;

import com.example.expense.model.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class ExpenseCategoryResponse {
    private Long id;
    private String name;

    public static ExpenseCategoryResponse convertOne(ExpenseCategory expenseCategory) {
        return new ExpenseCategoryResponse(expenseCategory.getId(), expenseCategory.getName());
    }

    public static List<ExpenseCategoryResponse> convertMany(List<ExpenseCategory> expenseCategories) {
        return expenseCategories.stream()
                .map(ExpenseCategoryResponse::convertOne)
                .collect(Collectors.toList());
    }

}
