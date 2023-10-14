package com.example.expense.controller;

import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Expense>> getAllExpensesByUserId(@PathVariable Long userId) {

        List<Expense> expenses = expenseService.getAllExpensesByUserId(userId);
        if (expenses != null) {
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense newExpense) {
        Expense createdExpense = expenseService.createExpense(newExpense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }


    @PutMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId, @RequestBody Expense updatedExpense) {
        if (!expenseId.equals(updatedExpense.getId())) {
            throw new IllegalArgumentException("Mismatched IDs in the URL and the request body.");
        }
        Expense updated = expenseService.updateExpense(updatedExpense);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

}
