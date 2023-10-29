package com.example.expense.service;

import com.example.expense.DTO.ExpenseRequest;
import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseCategoryRepository;
import com.example.expense.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    @Autowired
    private final ExpenseRepository expenseRepository;
    @Autowired
    private final ExpenseCategoryRepository expenseCategoryRepository;
    @Autowired
    private final UserService userService;

    public List<Expense> getAllUserExpenses() {
        User authUser = userService.getAuthorizedUser();
        return expenseRepository.findByUserId(authUser.getId());
    }

    public Expense createExpense(ExpenseRequest expenseRequest) {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Expense newExpense = new Expense();
        newExpense.setAmount(expenseRequest.getAmount());
        newExpense.setDate(expenseRequest.getDate());
        newExpense.setDescription(expenseRequest.getDescription());
        newExpense.setCategory(expenseCategoryRepository.findById(expenseRequest.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        newExpense.setUser(authUser);
        return expenseRepository.save(newExpense);
    }

    public Expense updateExpense(Long expenseId, ExpenseRequest expenseRequest) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));

        if (!existingExpense.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        existingExpense.setAmount(expenseRequest.getAmount());
        existingExpense.setDate(expenseRequest.getDate());
        existingExpense.setDescription(expenseRequest.getDescription());
        existingExpense.setCategory(expenseCategoryRepository.findById(expenseRequest.getCategoryId()).orElseThrow(()-> new EntityNotFoundException("Category not found")));
        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long expenseId) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));

        if (!existingExpense.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }
        expenseRepository.delete(existingExpense);
    }

}