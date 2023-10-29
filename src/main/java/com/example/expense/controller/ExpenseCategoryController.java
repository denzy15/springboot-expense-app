package com.example.expense.controller;

import com.example.expense.DTO.ExpenseCategoryRequest;
import com.example.expense.DTO.ExpenseCategoryResponse;
import com.example.expense.model.ExpenseCategory;
import com.example.expense.service.ExpenseCategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense-categories")
public class ExpenseCategoryController {
    @Autowired
    private final ExpenseCategoryService expenseCategoryService;


    @GetMapping
    public ResponseEntity<List<ExpenseCategoryResponse>> getAllUserExpenseCategories() {
        List<ExpenseCategory> expenseCategories = expenseCategoryService.getAllUserExpCategories();
        return ResponseEntity.ok(ExpenseCategoryResponse.convertMany(expenseCategories));
    }


    @PostMapping
    public ResponseEntity<ExpenseCategoryResponse> createExpenseCategory(@RequestBody @Valid ExpenseCategoryRequest category) {
        ExpenseCategory createdExpenseCategory = expenseCategoryService.createExpenseCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseCategoryResponse.convertOne(createdExpenseCategory));
    }


    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateExpenseCategory(@PathVariable Long categoryId, @RequestBody @Valid ExpenseCategoryRequest updatedCategory) {
        try {
            ExpenseCategory updated = expenseCategoryService.updateExpenseCategory(categoryId, updatedCategory);
            return ResponseEntity.ok(ExpenseCategoryResponse.convertOne(updated));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteExpenseCategory(@PathVariable Long categoryId) {
        try {
            expenseCategoryService.deleteExpenseCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
