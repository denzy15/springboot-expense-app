package com.example.expense.controller;

import com.example.expense.model.Income;
import com.example.expense.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<Income> createIncome(@RequestBody Income newIncome) {
        Income createdIncome = incomeService.createIncome(newIncome);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);

    }

    @GetMapping("/{userId}")
    public ResponseEntity< List<Income>> getAllIncomesByUserId(@PathVariable Long userId) {
        List<Income> incomes = incomeService.getAllIncomesByUserId(userId);
        return ResponseEntity.ok(incomes);
    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long incomeId, @RequestBody Income updatedIncome) {
        if (!incomeId.equals(updatedIncome.getId())) {
            throw new IllegalArgumentException("Mismatched IDs in the URL and the request body.");
        }
        Income updated = incomeService.updateIncome(updatedIncome);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId) {
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }
}
