package com.example.expense.controller;

import com.example.expense.DTO.IncomeRequest;
import com.example.expense.DTO.IncomeResponse;
import com.example.expense.model.Income;
import com.example.expense.service.IncomeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    @Autowired
    private final IncomeService incomeService;


    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllUserIncomes() {
        List<Income> incomes = incomeService.getAllUserIncomes();
        return ResponseEntity.ok(IncomeResponse.convertMany(incomes));
    }

    @PostMapping
    public ResponseEntity<?> createIncome(@RequestBody @Valid IncomeRequest incomeRequest) {
        try {
            Income createdIncome = incomeService.createIncome(incomeRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(IncomeResponse.convertOne(createdIncome));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<?> updateIncome(@PathVariable Long incomeId,
                                          @RequestBody @Valid IncomeRequest incomeRequest) {
        try {
            Income updated = incomeService.updateIncome(incomeId, incomeRequest);
            return ResponseEntity.ok(IncomeResponse.convertOne(updated));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long incomeId) {
        try {
            incomeService.deleteIncome(incomeId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
