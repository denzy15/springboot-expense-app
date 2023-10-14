package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Expense updateExpense(Expense updatedExpense) {
        Long expenseId = updatedExpense.getId();
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));

        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setDate(updatedExpense.getDate());
        existingExpense.setCategory(updatedExpense.getCategory());

        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long expenseId) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));

        expenseRepository.delete(existingExpense);
    }


}