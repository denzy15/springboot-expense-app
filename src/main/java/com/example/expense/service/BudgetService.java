package com.example.expense.service;

import com.example.expense.DTO.BudgetDTO;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.model.UserReference;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMemberRepository budgetMemberRepository;

    public Budget createBudget(UserReference owner, BudgetDTO budgetRequest) {
        Budget budget = new Budget();
        budget.setOwner(owner);
        budget.setName(budgetRequest.getName());
        budget.setShared(budgetRequest.isShared());
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long budgetId, BudgetDTO budgetRequest, Long currentUserId) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budget.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("У вас нет прав для изменения данного бюджета.");
        }

        budget.setName(budgetRequest.getName());
        budget.setShared(budgetRequest.isShared());

        return budgetRepository.save(budget);

    }

    public void deleteBudget(Long budgetId, Long currentUserId) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budget.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("У вас нет прав для изменения данного бюджета.");
        }

        budgetRepository.delete(budget);

    }

    public List<Budget> getBudgetsByUser(Long userId) {
        List<Budget> ownedBudgets = budgetRepository.findByOwnerId(userId);
        List<Budget> memberBudgets = budgetMemberRepository.findByBudgetId(userId)
                .stream()
                .map(BudgetMember::getBudget)
                .toList();

        List<Budget> allBudgets = new ArrayList<>();
        allBudgets.addAll(ownedBudgets);
        allBudgets.addAll(memberBudgets);
        return allBudgets;
    }
}
