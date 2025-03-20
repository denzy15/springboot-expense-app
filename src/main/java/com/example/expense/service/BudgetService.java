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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMemberRepository budgetMemberRepository;
    private final UserReferenceRepository userReferenceRepository;
    private final TransactionService transactionService;

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

    @Transactional
    public void changeBudgetAccess(Long budgetId, Boolean shared, Long currentUserId) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budget.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("У вас нет прав для изменения данного бюджета.");
        }

        if (!shared) {
            budgetMemberRepository.deleteByBudgetId(budgetId);
            budget.getMembers().clear(); // Удаляем участников из объекта, чтобы JPA правильно синхронизировал состояние

        }

        budget.setShared(shared);

    }

    public Budget renameBudget(Long budgetId, String budgetTitle, Long currentUserId) {

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budget.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("У вас нет прав для изменения данного бюджета.");
        }

        budget.setName(budgetTitle);

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
        List<Budget> memberBudgets = budgetMemberRepository.findByUserId(userId)
                .stream()
                .map(BudgetMember::getBudget)
                .toList();

        Set<Budget> allBudgets = new HashSet<>();
        allBudgets.addAll(ownedBudgets);
        allBudgets.addAll(memberBudgets);
        return new ArrayList<>(allBudgets);
    }

    public BudgetResponseDTO getBudgetById(Long budgetId, UserPrincipal currentUser) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (!budgetUtils.hasAccessToBudget(budgetId, currentUser.getId())) {
            throw new AccessDeniedException("У вас нет доступа к этому бюджету");
        }

        List<Transaction> recentTransactions = transactionService.getLast10Transactions(budgetId);


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
