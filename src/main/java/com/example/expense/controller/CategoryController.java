package com.example.expense.controller;

import com.example.expense.DTO.CategoryDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Category;
import com.example.expense.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody CategoryDTO categoryRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Category category = categoryService.createCategory(categoryRequest, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long categoryId,
            @RequestParam String newName,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, newName, currentUser.getId()));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        categoryService.deleteCategory(categoryId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

}
