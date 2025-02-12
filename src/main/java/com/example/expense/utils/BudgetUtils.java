package com.example.expense.utils;

import com.example.expense.enums.Role;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BudgetUtils {
    private final BudgetRepository budgetRepository;
    private final BudgetMemberRepository budgetMemberRepository;


    public boolean isUserBudgetOwner(Long budgetId, Long activeUserId) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new EntityNotFoundException("Бюджет id=" + budgetId + " не найден"));

        return Objects.equals(budget.getOwner().getId(), activeUserId);
    }

    public boolean hasModifyAccess(Long budgetId, Long activeUserId) {
        // Ищем бюджет
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Бюджет id=" + budgetId + " не найден"));

        // Если пользователь — владелец, то есть доступ
        if (Objects.equals(budget.getOwner().getId(), activeUserId)) {
            return true;
        }

        // Проверяем, есть ли запись в BudgetMember с правами на изменение
        Optional<BudgetMember> memberOpt =
                budgetMemberRepository.findByBudgetIdAndUserId(budgetId, activeUserId);
        return memberOpt.isPresent() && memberOpt.get().getRole() == Role.READ_WRITE;
    }



}
