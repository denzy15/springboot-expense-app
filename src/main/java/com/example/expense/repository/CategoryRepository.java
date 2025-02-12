package com.example.expense.repository;

import com.example.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByBudgetId(Long budgetId);
    boolean existsByBudgetIdAndName(Long budgetId, String name);
}

