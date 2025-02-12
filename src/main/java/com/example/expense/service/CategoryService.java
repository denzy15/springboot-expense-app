package com.example.expense.service;

import com.example.expense.DTO.CategoryDTO;
import com.example.expense.model.Budget;
import com.example.expense.model.Category;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.utils.BudgetUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    private final BudgetUtils budgetUtils;

    public Category createCategory(CategoryDTO categoryRequest, Long userId) {
        Budget budget = budgetRepository.findById(categoryRequest.getBudgetId())
                .orElseThrow(() -> new EntityNotFoundException("Бюджет id=" + categoryRequest.getBudgetId() + " не найден"));

        if (!budgetUtils.hasModifyAccess(categoryRequest.getBudgetId(), userId)) {
            throw new AccessDeniedException("Вы не можете добавлять категории в этот бюджет");
        }

        if (categoryRepository.existsByBudgetIdAndName(categoryRequest.getBudgetId(), categoryRequest.getName())) {
            throw new IllegalArgumentException("Категория с таким именем уже существует в этом бюджете");
        }

        Category category = Category.builder()
                .budget(budget)
                .name(categoryRequest.getName())
                .categoryType(categoryRequest.getCategoryType())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> getCategories(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Бюджет id=" + budgetId + " не найден"));

        return categoryRepository.findByBudgetId(budgetId);
    }

    public Category updateCategory(Long categoryId, String newName, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Категория id=" + categoryId + " не найдена"));

        if (!budgetUtils.hasModifyAccess(category.getBudget().getId(), userId)) {
            throw new AccessDeniedException("Вы не можете изменять эту категорию");
        }

        category.setName(newName);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Категория id=" + categoryId + " не найдена"));

        if (!budgetUtils.hasModifyAccess(category.getBudget().getId(), userId)) {
            throw new AccessDeniedException("Вы не можете удалять эту категорию");
        }

        categoryRepository.delete(category);
    }
}

