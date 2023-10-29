package com.example.expense.controller;

import com.example.expense.DTO.IncomeCategoryRequest;
import com.example.expense.DTO.IncomeCategoryResponse;
import com.example.expense.model.IncomeCategory;
import com.example.expense.service.IncomeCategoryService;
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
@RequestMapping("/api/income-categories")
@RequiredArgsConstructor
public class IncomeCategoryController {
    @Autowired
    private final IncomeCategoryService incomeCategoryService;


    @GetMapping
    public ResponseEntity<List<IncomeCategoryResponse>> getAllUserIncomeCategory() {
        List<IncomeCategory> incomeCategories = incomeCategoryService.getAllUserIncomeCategories();
        return ResponseEntity.ok(IncomeCategoryResponse.convertMany(incomeCategories));
    }

    @PostMapping
    public ResponseEntity<?> createIncomeCategory(@RequestBody @Valid IncomeCategoryRequest categoryRequest) {
        IncomeCategory createdIncomeCategory = incomeCategoryService.createIncomeCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(IncomeCategoryResponse.convertOne(createdIncomeCategory));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateIncomeCategory(@PathVariable Long categoryId,
                                                  @RequestBody @Valid IncomeCategoryRequest updatedCategory) {
        try {
            IncomeCategory updated = incomeCategoryService.updateIncomeCategory(categoryId, updatedCategory);
            return ResponseEntity.ok(IncomeCategoryResponse.convertOne(updated));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteIncomeCategory(@PathVariable Long categoryId) {
        try {
            incomeCategoryService.deleteIncomeCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
