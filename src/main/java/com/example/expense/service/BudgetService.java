package com.example.expense.service;

import com.example.expense.DTO.BudgetRequestDTO;
import com.example.expense.DTO.BudgetResponseDTO;
import com.example.expense.DTO.UserPrincipal;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.model.Transaction;
import com.example.expense.model.UserReference;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.TransactionRepository;
import com.example.expense.repository.UserReferenceRepository;
import com.example.expense.utils.BudgetUtils;
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
    private final UserReferenceRepository userReferenceRepository;
    private final TransactionRepository transactionRepository;

    private final BudgetUtils budgetUtils;

    public Budget createBudget(UserPrincipal owner, BudgetRequestDTO budgetRequest) {
        UserReference userReference = userReferenceRepository.findById(owner.getId())
                .orElseGet(
                        () -> {
                            UserReference newUser = new UserReference(owner.getId(), owner.getEmail(), owner.getUsername());
                            return userReferenceRepository.save(newUser);
                        }
                );

        Budget budget = new Budget();
        budget.setOwner(userReference);
        budget.setName(budgetRequest.getName());
        budget.setShared(budgetRequest.isShared());
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long budgetId, BudgetRequestDTO budgetRequest, Long currentUserId) {

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

    public BudgetResponseDTO getBudgetById(Long budgetId, UserPrincipal currentUser) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budgetUtils.hasAccessToBudget(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("У вас нет доступа к этому бюджету");
        }

        List<Transaction> recentTransactions = transactionRepository.findTop10ByAccount_Budget_IdOrderByCreatedAtDesc(budgetId);


        return new BudgetResponseDTO(
                budgetId,
                budget.getName(),
                budget.getOwner(),
                budget.isShared(),
                budget.getAccounts(),
                budget.getCategories(),
                budget.getMembers(),
                recentTransactions
        );

    }
}
