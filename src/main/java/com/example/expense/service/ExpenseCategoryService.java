package com.example.expense.service;

import com.example.expense.model.ExpenseCategory;
import com.example.expense.repository.ExpenseCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategory createExpenseCategory(ExpenseCategory category) {
        return expenseCategoryRepository.save(category);
    }

    public List<ExpenseCategory> getAllExpenseCategoriesByUserId(Long userId) {
        return expenseCategoryRepository.findByUserId(userId);
    }

    public ExpenseCategory updateExpenseCategory(ExpenseCategory category) {
        ExpenseCategory existingExpenseCategory = expenseCategoryRepository.findById(category.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Expense category not found with id " + category.getId())
                );
        existingExpenseCategory.setName(category.getName());
        return expenseCategoryRepository.save(existingExpenseCategory);
    }

    public void deleteExpenseCategory(Long expenseCategoryId) {
        ExpenseCategory existingExpenseCategory = expenseCategoryRepository.findById(expenseCategoryId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Expense category not found with id " + expenseCategoryId)
                );
        expenseCategoryRepository.delete(existingExpenseCategory);
    }

}