package com.example.expense.service;

import com.example.expense.DTO.ExpenseCategoryRequest;
import com.example.expense.model.ExpenseCategory;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {
    @Autowired
    private final ExpenseCategoryRepository expenseCategoryRepository;
    @Autowired
    private final UserService userService;

    public List<ExpenseCategory> getAllUserExpCategories() {
        User authUser = userService.getAuthorizedUser();
        return expenseCategoryRepository.findByUserId(authUser.getId());
    }

    public ExpenseCategory createExpenseCategory(ExpenseCategoryRequest category) {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        ExpenseCategory newExpCategory = new ExpenseCategory();
        newExpCategory.setName(category.getName());
        newExpCategory.setUser(authUser);
        return expenseCategoryRepository.save(newExpCategory);
    }

    public ExpenseCategory updateExpenseCategory(Long ecId, ExpenseCategoryRequest category) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        ExpenseCategory existingExpenseCategory =
                expenseCategoryRepository.findById(ecId).orElseThrow(() -> new EntityNotFoundException("Expense " +
                        "category not found with Id " + ecId));

        if (!existingExpenseCategory.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }

        existingExpenseCategory.setName(category.getName());
        return expenseCategoryRepository.save(existingExpenseCategory);
    }

    public void deleteExpenseCategory(Long ecId) throws AccessDeniedException {
        User authUser = userService.getAuthorizedUser();

        if (authUser == null) {
            throw new NullPointerException("Authorization error");
        }

        ExpenseCategory existingExpenseCategory =
                expenseCategoryRepository.findById(ecId).orElseThrow(() -> new EntityNotFoundException("Expense " +
                        "category not found with id " + ecId));

        if (!existingExpenseCategory.getUser().equals(authUser)) {
            throw new AccessDeniedException("You do not have access to modify this data");
        }
        expenseCategoryRepository.delete(existingExpenseCategory);
    }

}