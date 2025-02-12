package com.example.expense.repository;

import com.example.expense.model.BudgetMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetMemberRepository extends JpaRepository<BudgetMember, Long> {
    List<BudgetMember> findByBudgetId(Long budgetId);
    boolean existsByBudgetIdAndUserId(Long budgetId, Long userId);
    Optional<BudgetMember> findByBudgetIdAndUserId(Long budgetId, Long userId);
}

