package com.example.expense.controller;

import com.example.expense.model.IncomeCategory;
import com.example.expense.service.IncomeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/income-categories")
public class IncomeCategoryController {
    private final IncomeCategoryService incomeCategoryService;

    @Autowired
    public IncomeCategoryController(IncomeCategoryService incomeCategoryService) {
        this.incomeCategoryService = incomeCategoryService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<IncomeCategory>> getAllIncomeCategoryByUserId(@PathVariable Long userId) {
        List<IncomeCategory> incomeCategories = incomeCategoryService.getAllIncomeCategoriesByUserId(userId);
        if (incomeCategories != null) {
            return ResponseEntity.ok(incomeCategories);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<IncomeCategory> createIncomeCategory(@RequestBody IncomeCategory category) {
        IncomeCategory createdIncomeCategory = incomeCategoryService.createIncomeCategory(category);
        return ResponseEntity.ok(createdIncomeCategory);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<IncomeCategory> updateIncomeCategory(@PathVariable Long categoryId, @RequestBody IncomeCategory updatedCategory) {

        if (!categoryId.equals(updatedCategory.getId())) {
            throw new IllegalArgumentException("Mismatched IDs in the URL and the request body.");
        }

        IncomeCategory updated = incomeCategoryService.updateIncomeCategory(updatedCategory);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteIncomeCategory(@PathVariable Long categoryId) {
        incomeCategoryService.deleteIncomeCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
