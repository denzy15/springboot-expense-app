package com.example.expense.service;

import com.example.expense.model.ExpenseCategory;
import com.example.expense.repository.ExpenseCategoryRepository;
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

}