package com.example.expense.utils;

import com.example.expense.enums.Role;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Setter
@Getter
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

    public boolean hasAccessToBudget(Long budgetId, Long activeUserId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Бюджет id=" + budgetId + " не найден"));

        if (Objects.equals(budget.getOwner().getId(), activeUserId)) {
            return true;
        }

        return budgetMemberRepository.findByBudgetIdAndUserId(budgetId, activeUserId).isPresent();
    }

    public boolean isAbleToKick(Long budgetId, Long activeUserId, BudgetMember member) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(
                () -> new EntityNotFoundException("Бюджет не найден")
        );

        if (Objects.equals(activeUserId, member.getId())) return  false;

        if (budget.getOwner().getId().equals(activeUserId)) return true;

        BudgetMember activeUser = budgetMemberRepository.findByBudgetIdAndUserId(budgetId, activeUserId).orElseThrow(
                ()-> new EntityNotFoundException("Вы не являетесь участником бюджета")
        );

        Role memberRole = member.getRole();
        Role activeUserRole = activeUser.getRole();

        if (activeUserRole.equals(Role.READ) || activeUserRole.equals(memberRole)) return false;

        return activeUserRole.equals(Role.READ_WRITE);

    }

}
