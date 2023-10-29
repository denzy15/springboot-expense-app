package com.example.expense.controller;

import com.example.expense.DTO.ExpenseRequest;
import com.example.expense.DTO.ExpenseResponse;
import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;
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
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private final ExpenseService expenseService;


    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllUserExpenses() {
        List<Expense> expenses = expenseService.getAllUserExpenses();
        return ResponseEntity.ok(ExpenseResponse.convertMany(expenses));
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody @Valid ExpenseRequest newExpense) {

        try {
            Expense createdExpense = expenseService.createExpense(newExpense);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseResponse.convertOne(createdExpense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<?> updateExpense(@PathVariable Long expenseId,
                                           @RequestBody @Valid ExpenseRequest updatedExpense) {
        try {
            Expense updated = expenseService.updateExpense(expenseId, updatedExpense);
            return ResponseEntity.ok(ExpenseResponse.convertOne(updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


}
