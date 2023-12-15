package com.example.expense.DTO;

import com.example.expense.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private String description;
    private LocalDate date;
    private double amount;
    private ExpenseCategoryResponse category;

    public static ExpenseResponse convertOne(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getDescription(), expense.getDate(), expense.getAmount(),
                new ExpenseCategoryResponse(expense.getCategory().getId(), expense.getCategory().getName()));
    }

    public static List<ExpenseResponse> convertMany(List<Expense> expenses) {
        return expenses.stream()
                .map(ExpenseResponse::convertOne)
                .collect(Collectors.toList());

    }

}
