package com.example.expense.controller;

import com.example.expense.model.ExpenseCategory;
import com.example.expense.service.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-categories")
public class ExpenseCategoryController {
    private final ExpenseCategoryService expenseCategoryService;

    @Autowired
    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ExpenseCategory>> getAllExpenseCategoriesByUserId(@PathVariable Long userId) {
        List<ExpenseCategory> expenseCategories = expenseCategoryService.getAllExpenseCategoriesByUserId(userId);
        if (expenseCategories != null) {

            return ResponseEntity.ok(expenseCategories);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<ExpenseCategory> createExpenseCategory(@RequestBody ExpenseCategory category) {
        ExpenseCategory createdExpenseCategory = expenseCategoryService.createExpenseCategory(category);
        return ResponseEntity.ok(createdExpenseCategory);
    }


    @PutMapping("/{categoryId}")
    public ResponseEntity<ExpenseCategory> updateExpenseCategory(@PathVariable Long categoryId, @RequestBody ExpenseCategory updatedCategory) {
        if (!categoryId.equals(updatedCategory.getId())) {
            throw new IllegalArgumentException("Mismatched IDs in the URL and the request body.");
        }
        ExpenseCategory updated = expenseCategoryService.updateExpenseCategory(updatedCategory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteExpenseCategory(@PathVariable Long categoryId) {
        expenseCategoryService.deleteExpenseCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
