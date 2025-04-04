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

    public BudgetMember changeMemberRole(Long budgetId, BudgetMemberDTO member, Long activeUserId) {

        BudgetMember existingMember = budgetMemberRepository.findByBudgetIdAndUserId(budgetId, member.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с id " + member.getUserId() + " нет в бюджете " + budgetId));

        if (!budgetUtils.isAbleToEditMember(budgetId, activeUserId, member.getUserId(), member.getRole())) {
            throw new PersistenceException("Недостаточно прав");
        }

        existingMember.setRole(member.getRole());
        return budgetMemberRepository.save(existingMember);

    }

    public BudgetMember addMember(Long budgetId, BudgetMemberDTO budgetMemberDTO, Long activeUserId) {
        if (budgetMemberRepository.existsByBudgetIdAndUserId(budgetId, budgetMemberDTO.getUserId())) {
            throw new IllegalArgumentException("Этот пользователь уже является участником бюджета.");
        }

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Бюджет не найден"));

        if (!budgetUtils.hasModifyAccess(budgetId, activeUserId)) {
            throw new PersistenceException("Недостаточно прав");
        }

        // Проверяем, есть ли уже UserReference в базе
        UserReference userReference = userReferenceRepository.findById(budgetMemberDTO.getUserId())
                .orElseGet(() -> {
                    // Если нет, создаем и сохраняем
                    UserReference newUser = new UserReference(budgetMemberDTO.getUserId(), budgetMemberDTO.getEmail(), budgetMemberDTO.getUsername());
                    return userReferenceRepository.save(newUser);
                });

        BudgetMember member = new BudgetMember();
        member.setBudget(budget);
        member.setUser(userReference);
        member.setRole(budgetMemberDTO.getRole());

        return budgetMemberRepository.save(member);
    }


    public void kickMember(Long budgetId, Long memberId, Long activeUserId) {
        BudgetMember member = budgetMemberRepository.findByBudgetIdAndUserId(budgetId, memberId).orElseThrow(
                () -> new EntityNotFoundException("Участник не найден")
        );

        if (!budgetUtils.isAbleToEditMember(budgetId, activeUserId, member.getUser().getId(), member.getRole())) {
            throw new PersistenceException("Вы не можете исключить данного участника");
        }

        budgetMemberRepository.delete(member);
    }
}

