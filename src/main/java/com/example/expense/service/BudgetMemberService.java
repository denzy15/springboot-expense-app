package com.example.expense.service;

import com.example.expense.DTO.BudgetMemberDTO;
import com.example.expense.enums.Role;
import com.example.expense.model.Budget;
import com.example.expense.model.BudgetMember;
import com.example.expense.model.UserReference;
import com.example.expense.repository.BudgetMemberRepository;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.UserReferenceRepository;
import com.example.expense.utils.BudgetUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BudgetMemberService {

    private final BudgetRepository budgetRepository;
    private final BudgetMemberRepository budgetMemberRepository;
    private final UserReferenceRepository userReferenceRepository;
    private final BudgetUtils budgetUtils;

    public BudgetMember changeMemberRole(Long budgetId, Long memberId, Role role, Long activeUserId) {

        BudgetMember existingMember = budgetMemberRepository.findByBudgetIdAndUserId(budgetId, memberId).orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + memberId + " нет в бюджете " + budgetId));

        if (!budgetUtils.isUserBudgetOwner(budgetId, activeUserId)) {
            throw new PersistenceException("Не достаточно прав, вы не являетесь владельцем бюджета");
        }

        existingMember.setRole(role);
        return budgetMemberRepository.save(existingMember);


    }

    public BudgetMember addMember(Long budgetId, BudgetMemberDTO budgetMember, Long activeUserId) {
        if (budgetMemberRepository.existsByBudgetIdAndUserId(budgetId, budgetMember.getId())) {
            throw new IllegalArgumentException("Этот пользователь уже является участником бюджета.");
        }

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Бюджет не найден"));

        if (!budgetUtils.isUserBudgetOwner(budgetId, activeUserId)) {
            throw new PersistenceException("Недостаточно прав, вы не являетесь владельцем бюджета");
        }

        // Проверяем, есть ли уже UserReference в базе
        UserReference userReference = userReferenceRepository.findById(budgetMember.getId())
                .orElseGet(() -> {
                    // Если нет, создаем и сохраняем
                    UserReference newUser = new UserReference(budgetMember.getId(), budgetMember.getEmail(), budgetMember.getUsername());
                    return userReferenceRepository.save(newUser);
                });

        BudgetMember member = new BudgetMember();
        member.setBudget(budget);
        member.setUser(userReference);
        member.setRole(budgetMember.getRole());

        return budgetMemberRepository.save(member);
    }


    public void kickMember(Long budgetId, Long memberId, Long activeUserId) {
        BudgetMember member = budgetMemberRepository.findByBudgetIdAndUserId(budgetId, memberId).orElseThrow(
                () -> new EntityNotFoundException("Участник не найден")
        );

        if (!budgetUtils.isAbleToKick(budgetId, activeUserId, member)) {
            throw new PersistenceException("Вы не можете исключить данного участника");
        }

        budgetMemberRepository.delete(member);
    }
}

